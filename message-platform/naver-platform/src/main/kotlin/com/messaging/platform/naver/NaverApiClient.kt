package com.messaging.platform.naver

import com.messaging.core.naver.domain.NaverSendResult
import com.messaging.platform.naver.config.NaverApi
import com.messaging.platform.naver.config.NaverProperties
import com.messaging.platform.naver.dto.NaverResponse
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.util.retry.Retry
import java.time.Duration
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class NaverApiClient(
    @param:Qualifier("naverWebClient") private val webClient: WebClient,
    @param:Qualifier("naverCircuitBreaker") private val circuitBreaker: CircuitBreaker,
    @param:Qualifier("naverRetry") private val retry: Retry,
    private val config: NaverProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun send(
        path: String,
        request: Any,
        messageId: String
    ): NaverSendResult {
        val timestamp = System.currentTimeMillis().toString()
        val signature = makeSignature(path, timestamp)

        return try {
            val response = webClient.post()
                .uri("${config.baseUrl}$path")
                .contentType(MediaType.APPLICATION_JSON)
                .header(NaverApi.HEADER_TIMESTAMP, timestamp)
                .header(NaverApi.HEADER_ACCESS_KEY, config.accessKey)
                .header(NaverApi.HEADER_SIGNATURE, signature)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NaverResponse::class.java)
                .timeout(Duration.ofMillis(config.timeout))
                .retryWhen(retry)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .awaitSingleOrNull()

            handleResponse(response, messageId)
        } catch (e: WebClientResponseException) {
            log.error("HTTP error from Naver API: messageId={}, status={}", messageId, e.statusCode)
            NaverSendResult.fail("HTTP_${e.statusCode.value()}", e.responseBodyAsString.ifBlank { "HTTP error" })
        } catch (e: Exception) {
            log.error("Failed to send via Naver: messageId={}, error={}", messageId, e.message)
            NaverSendResult.naverApiError()
        }
    }

    private fun handleResponse(response: NaverResponse?, messageId: String): NaverSendResult {
        if (response == null) {
            return NaverSendResult.fail("EMPTY_RESPONSE", "Empty response from Naver")
        }

        if (response.isSuccess()) {
            log.info("Sent successfully via Naver: messageId={}", messageId)
            return NaverSendResult.success(response.requestId)
        }

        log.warn("Naver API returned error: messageId={}, code={}", messageId, response.statusCode)
        return NaverSendResult.fail(response.statusCode, response.statusName)
    }

    private fun makeSignature(path: String, timestamp: String): String {
        val message = buildString {
            append("POST ")
            append(path)
            append("\n")
            append(timestamp)
            append("\n")
            append(config.accessKey)
        }

        val signingKey = SecretKeySpec(config.secretKey.toByteArray(Charsets.UTF_8), NaverApi.SIGNATURE_ALGORITHM)
        val mac = Mac.getInstance(NaverApi.SIGNATURE_ALGORITHM)
        mac.init(signingKey)
        val rawHmac = mac.doFinal(message.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(rawHmac)
    }
}
