package com.messaging.platform.skt

import com.messaging.core.sms.domain.SmsSendResult
import com.messaging.platform.skt.config.SktApi
import com.messaging.platform.skt.config.SktConfig
import com.messaging.platform.skt.config.SktErrorCode
import com.messaging.platform.skt.dto.SktResponse
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.Duration

/**
 * SKT API 호출 공통 클라이언트
 */
@Component
class SktApiClient(
    private val webClient: WebClient,
    private val circuitBreaker: CircuitBreaker,
    private val config: SktConfig
) {
    private val log = LoggerFactory.getLogger(javaClass)

    val isEnabled: Boolean get() = config.enabled

    /**
     * SKT API 호출
     */
    suspend fun <T : Any> send(
        path: String,
        request: T,
        messageId: String,
        messageType: String
    ): SmsSendResult {
        return try {
            val response = webClient.post()
                .uri("${config.baseUrl}$path")
                .contentType(MediaType.APPLICATION_JSON)
                .header(SktApi.HEADER_API_KEY, config.apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SktResponse::class.java)
                .timeout(Duration.ofMillis(config.timeout))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .awaitSingleOrNull()

            handleResponse(response, messageId, messageType)
        } catch (e: WebClientResponseException) {
            log.error("HTTP error from SKT API: messageId={}, status={}", messageId, e.statusCode)
            SmsSendResult.fail("HTTP_${e.statusCode.value()}", e.responseBodyAsString.ifBlank { "HTTP error" })
        } catch (e: Exception) {
            log.error("Failed to send {} via SKT: messageId={}, error={}", messageType, messageId, e.message)
            SmsSendResult.retryable("EXCEPTION", e.message ?: "Unknown error")
        }
    }

    private fun handleResponse(
        response: SktResponse?,
        messageId: String,
        messageType: String
    ): SmsSendResult {
        if (response == null) {
            log.error("Empty response from SKT API: messageId={}", messageId)
            return SmsSendResult.fail("EMPTY_RESPONSE", "Empty response from SKT")
        }

        if (response.isSuccess()) {
            log.info("{} sent successfully via SKT: messageId={}", messageType, messageId)
            return SmsSendResult.success(response.resultCode, response.resultMsg)
        }

        log.warn("SKT API returned error: messageId={}, code={}", messageId, response.resultCode)
        return if (SktErrorCode.isRetryable(response.resultCode)) {
            SmsSendResult.retryable(response.resultCode, response.resultMsg)
        } else {
            SmsSendResult.fail(response.resultCode, response.resultMsg)
        }
    }
}
