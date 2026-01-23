package com.messaging.platform.kt

import com.messaging.core.sms.domain.SmsSendResult
import com.messaging.platform.kt.config.KtApi
import com.messaging.platform.kt.config.KtConfig
import com.messaging.platform.kt.config.KtErrorCode
import com.messaging.platform.kt.dto.KtResponse
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
 * KT API 호출 공통 클라이언트
 */
@Component
class KtApiClient(
    private val webClient: WebClient,
    private val circuitBreaker: CircuitBreaker,
    private val config: KtConfig
) {
    private val log = LoggerFactory.getLogger(javaClass)

    val isEnabled: Boolean get() = config.enabled

    /**
     * KT API 호출
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
                .header(KtApi.HEADER_AUTHORIZATION, "${KtApi.BEARER_PREFIX}${config.apiKey}")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(KtResponse::class.java)
                .timeout(Duration.ofMillis(config.timeout))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .awaitSingleOrNull()

            handleResponse(response, messageId, messageType)
        } catch (e: WebClientResponseException) {
            log.error("HTTP error from KT API: messageId={}, status={}", messageId, e.statusCode)
            SmsSendResult.fail("HTTP_${e.statusCode.value()}", e.responseBodyAsString.ifBlank { "HTTP error" })
        } catch (e: Exception) {
            log.error("Failed to send via KT: messageId={}, error={}", messageId, e.message)
            SmsSendResult.retryable("EXCEPTION", e.message ?: "Unknown error")
        }
    }

    private fun handleResponse(
        response: KtResponse?,
        messageId: String,
        messageType: String
    ): SmsSendResult {
        if (response == null) {
            return SmsSendResult.fail("EMPTY_RESPONSE", "Empty response from KT")
        }

        if (response.isSuccess()) {
            log.info("{} sent successfully via KT: messageId={}", messageType, messageId)
            return SmsSendResult.success(response.code, response.message)
        }

        log.warn("KT API returned error: messageId={}, code={}", messageId, response.code)
        return if (KtErrorCode.isRetryable(response.code)) {
            SmsSendResult.retryable(response.code, response.message)
        } else {
            SmsSendResult.fail(response.code, response.message)
        }
    }
}
