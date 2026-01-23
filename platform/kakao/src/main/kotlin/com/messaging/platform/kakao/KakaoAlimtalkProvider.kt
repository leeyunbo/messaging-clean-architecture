package com.messaging.platform.kakao

import com.messaging.core.kakao.domain.AlimtalkProvider
import com.messaging.core.kakao.domain.AlimtalkRequest
import com.messaging.core.kakao.domain.KakaoSendResult
import com.messaging.library.idgen.MessageIdGenerator
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.Duration

/**
 * 카카오 알림톡 Provider 구현체
 */
@Component
class KakaoAlimtalkProvider(
    @Qualifier("kakaoWebClient") private val webClient: WebClient,
    @Qualifier("kakaoCircuitBreaker") private val circuitBreaker: CircuitBreaker,
    @param:Value("\${kakao.alimtalk.base-url:https://alimtalk-api.kakao.com}") private val baseUrl: String,
    @param:Value("\${kakao.alimtalk.api-key:}") private val apiKey: String,
    @param:Value("\${kakao.alimtalk.enabled:true}") private val enabled: Boolean
) : AlimtalkProvider {
    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun send(request: AlimtalkRequest): KakaoSendResult {
        if (!enabled) {
            return KakaoSendResult.fail("DISABLED", "Kakao Alimtalk provider is disabled")
        }

        val serialNumber = MessageIdGenerator.generate()
        val requestBody = request.variables.toMutableMap().apply {
            put("serialNumber", serialNumber)
        }

        val timeout = request.timeout?.let { Duration.ofMillis(it + 1000) }
            ?: Duration.ofSeconds(10)

        return try {
            val response = webClient.post()
                .uri("$baseUrl/v1/send")
                .header("Authorization", "Bearer $apiKey")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono<KakaoApiResponse>()
                .timeout(timeout)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .awaitSingle()

            log.info("Kakao API response: messageId={}, code={}", request.messageId, response.resultCode)

            if (response.resultCode == "0000") {
                KakaoSendResult.success(response.resultCode, response.resultMsg ?: "Success", serialNumber)
            } else if (isRetryable(response.resultCode)) {
                KakaoSendResult.retryable(response.resultCode, response.resultMsg ?: "Retryable error")
            } else {
                KakaoSendResult.fail(response.resultCode, response.resultMsg ?: "Failed")
            }
        } catch (e: Exception) {
            log.error("Kakao API call failed: messageId={}, error={}", request.messageId, e.message)
            KakaoSendResult.retryable("API_ERROR", e.message ?: "API call failed")
        }
    }

    private fun isRetryable(code: String): Boolean = code in setOf(
        "9999",  // 시스템 오류
        "5000",  // 타임아웃
        "5001"   // 일시적 장애
    )
}

/**
 * 카카오 API 응답
 */
data class KakaoApiResponse(
    val resultCode: String,
    val resultMsg: String?
)
