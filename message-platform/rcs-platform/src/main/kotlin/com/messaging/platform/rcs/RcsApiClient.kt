package com.messaging.platform.rcs

import com.messaging.core.rcs.domain.RcsSendResult
import com.messaging.platform.rcs.config.RcsApi
import com.messaging.platform.rcs.config.RcsProperties
import com.messaging.platform.rcs.dto.RcsResponse
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

@Component
class RcsApiClient(
    @param:Qualifier("rcsWebClient") private val webClient: WebClient,
    @param:Qualifier("rcsCircuitBreaker") private val circuitBreaker: CircuitBreaker,
    @param:Qualifier("rcsRetry") private val retry: Retry,
    private val config: RcsProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun sendStandalone(request: Any, messageId: String): RcsSendResult {
        return send(RcsApi.STANDALONE_PATH, request, messageId)
    }

    suspend fun sendCarousel(request: Any, messageId: String): RcsSendResult {
        return send(RcsApi.CAROUSEL_PATH, request, messageId)
    }

    private suspend fun send(
        path: String,
        request: Any,
        messageId: String
    ): RcsSendResult {
        return try {
            val response = webClient.post()
                .uri("${config.baseUrl}$path")
                .contentType(MediaType.APPLICATION_JSON)
                .header(RcsApi.HEADER_API_KEY, config.apiKey)
                .header(RcsApi.HEADER_BRAND_ID, config.brandId)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(RcsResponse::class.java)
                .timeout(Duration.ofMillis(config.timeout))
                .retryWhen(retry)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .awaitSingleOrNull()

            handleResponse(response, messageId)
        } catch (e: WebClientResponseException) {
            log.error("HTTP error from RCS API: messageId={}, status={}", messageId, e.statusCode)
            RcsSendResult.fail("HTTP_${e.statusCode.value()}", e.responseBodyAsString.ifBlank { "HTTP error" })
        } catch (e: Exception) {
            log.error("Failed to send via RCS: messageId={}, error={}", messageId, e.message)
            RcsSendResult.rcsApiError()
        }
    }

    private fun handleResponse(response: RcsResponse?, messageId: String): RcsSendResult {
        if (response == null) {
            return RcsSendResult.fail("EMPTY_RESPONSE", "Empty response from RCS")
        }

        if (response.isSuccess()) {
            log.info("Sent successfully via RCS: messageId={}", messageId)
            return RcsSendResult.success(response.requestId)
        }

        log.warn("RCS API returned error: messageId={}, code={}", messageId, response.resultCode)
        return RcsSendResult.fail(response.resultCode, response.resultMessage)
    }
}
