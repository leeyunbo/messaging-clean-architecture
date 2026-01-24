package com.messaging.platform.kakaodirect

import com.messaging.core.kakao.domain.KakaoSendResult
import com.messaging.platform.kakaodirect.config.KakaoDirectApi
import com.messaging.platform.kakaodirect.config.KakaoDirectConfig
import com.messaging.platform.kakaodirect.config.KakaoDirectErrorCode
import com.messaging.platform.kakaodirect.dto.KakaoDirectResponse
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
 * 카카오 다이렉트 브랜드메시지 API 호출 클라이언트
 */
@Component
class KakaoDirectApiClient(
    private val webClient: WebClient,
    private val circuitBreaker: CircuitBreaker,
    private val config: KakaoDirectConfig
) {
    private val log = LoggerFactory.getLogger(javaClass)

    val isEnabled: Boolean get() = config.enabled

    /**
     * 브랜드메시지 API 호출
     * @param path BrandMessageType에 따른 URI path
     * @param body 카카오에 전송할 요청 body (variables)
     * @param messageId 우리 시스템의 메시지 ID
     * @param serialNumber X-Serial-Number 헤더값
     */
    suspend fun send(
        path: String,
        body: Map<String, Any?>,
        messageId: String,
        serialNumber: String
    ): KakaoSendResult {
        return try {
            log.debug("Sending BrandMessage: messageId={}, serialNumber={}, path={}", messageId, serialNumber, path)

            val response = webClient.post()
                .uri("${config.baseUrl}$path")
                .contentType(MediaType.APPLICATION_JSON)
                .header(KakaoDirectApi.HEADER_API_KEY, config.apiKey)
                .header(KakaoDirectApi.HEADER_SECRET_KEY, config.secretKey)
                .header(KakaoDirectApi.HEADER_SERIAL_NUMBER, serialNumber)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(KakaoDirectResponse::class.java)
                .timeout(Duration.ofMillis(config.timeout))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .awaitSingleOrNull()

            handleResponse(response, messageId)
        } catch (e: WebClientResponseException) {
            log.error("HTTP error from Kakao Direct API: messageId={}, status={}", messageId, e.statusCode)
            KakaoSendResult.fail("HTTP_${e.statusCode.value()}", e.responseBodyAsString.ifBlank { "HTTP error" })
        } catch (e: Exception) {
            log.error("Failed to send BrandMessage: messageId={}, error={}", messageId, e.message)
            KakaoSendResult.retryable("EXCEPTION", e.message ?: "Unknown error")
        }
    }

    private fun handleResponse(response: KakaoDirectResponse?, messageId: String): KakaoSendResult {
        if (response == null) {
            return KakaoSendResult.fail("EMPTY_RESPONSE", "Empty response from Kakao Direct")
        }

        if (response.isSuccess()) {
            log.info("BrandMessage sent successfully: messageId={}", messageId)
            return KakaoSendResult.success(response.code, response.message)
        }

        log.warn("Kakao Direct API returned error: messageId={}, code={}", messageId, response.code)
        return if (KakaoDirectErrorCode.isRetryable(response.code)) {
            KakaoSendResult.retryable(response.code, response.message)
        } else {
            KakaoSendResult.fail(response.code, response.message)
        }
    }
}
