package com.messaging.platform.kakao

import com.messaging.core.kakao.domain.KakaoSendResult
import com.messaging.platform.kakao.config.KakaoAlimtalkApi
import com.messaging.platform.kakao.config.KakaoConfig
import com.messaging.platform.kakao.config.KakaoErrorCode
import com.messaging.platform.kakao.dto.KakaoResponse
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import java.time.Duration

/**
 * 카카오 알림톡 API 호출 클라이언트
 */
@Component
class KakaoAlimtalkApiClient(
    @Qualifier("kakaoWebClient") private val webClient: WebClient,
    @Qualifier("kakaoCircuitBreaker") private val circuitBreaker: CircuitBreaker,
    private val config: KakaoConfig
) {
    private val log = LoggerFactory.getLogger(javaClass)

    val isEnabled: Boolean get() = config.enabled

    /**
     * 알림톡 API 호출
     * @param body 카카오에 전송할 요청 body (variables + serialNumber)
     * @param messageId 우리 시스템의 메시지 ID
     * @param serialNumber 카카오 요청용 고유 번호
     */
    suspend fun send(
        body: Map<String, Any?>,
        messageId: String,
        serialNumber: String
    ): KakaoSendResult {
        return try {
            log.debug("Sending Alimtalk: messageId={}, serialNumber={}", messageId, serialNumber)

            val response = webClient.post()
                .uri("${config.baseUrl}${KakaoAlimtalkApi.SEND_PATH}")
                .contentType(MediaType.APPLICATION_JSON)
                .header(KakaoAlimtalkApi.HEADER_API_KEY, config.apiKey)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(KakaoResponse::class.java)
                .timeout(Duration.ofMillis(config.timeout))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .awaitSingleOrNull()

            handleResponse(response, messageId)
        } catch (e: WebClientResponseException) {
            log.error("HTTP error from Kakao Alimtalk API: messageId={}, status={}", messageId, e.statusCode)
            KakaoSendResult.fail("HTTP_${e.statusCode.value()}", e.responseBodyAsString.ifBlank { "HTTP error" })
        } catch (e: Exception) {
            log.error("Failed to send Alimtalk: messageId={}, error={}", messageId, e.message)
            KakaoSendResult.retryable("EXCEPTION", e.message ?: "Unknown error")
        }
    }

    private fun handleResponse(response: KakaoResponse?, messageId: String): KakaoSendResult {
        if (response == null) {
            return KakaoSendResult.fail("EMPTY_RESPONSE", "Empty response from Kakao")
        }

        if (response.isSuccess()) {
            log.info("Alimtalk sent successfully: messageId={}", messageId)
            return KakaoSendResult.success(response.code.toString(), response.message)
        }

        log.warn("Kakao Alimtalk API returned error: messageId={}, code={}", messageId, response.code)
        return if (KakaoErrorCode.isRetryable(response.code)) {
            KakaoSendResult.retryable(response.code.toString(), response.message)
        } else {
            KakaoSendResult.fail(response.code.toString(), response.message)
        }
    }
}
