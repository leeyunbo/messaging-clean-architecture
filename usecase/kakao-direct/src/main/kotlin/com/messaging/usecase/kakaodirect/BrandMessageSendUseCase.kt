package com.messaging.usecase.kakaodirect

import com.messaging.core.kakao.domain.BrandMessageProvider
import com.messaging.core.kakao.domain.BrandMessageRequest
import com.messaging.core.kakao.domain.KakaoSendResult
import com.messaging.core.report.domain.Report
import com.messaging.core.report.domain.ReportPublisher
import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration

/**
 * 카카오 브랜드메시지 발송 유스케이스 (Kakao Direct)
 */
@Service
class BrandMessageSendUseCase(
    private val brandMessageProvider: BrandMessageProvider,
    private val reportPublisher: ReportPublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val retry = Retry.of("kakao-direct-brandmessage-send", RetryConfig.custom<KakaoSendResult>()
        .maxAttempts(3)
        .waitDuration(Duration.ofSeconds(1))
        .retryOnResult { it.retryable }
        .failAfterMaxAttempts(true)
        .build())

    suspend fun send(request: BrandMessageRequest): KakaoSendResult {
        log.info("Processing BrandMessage send: messageId={}, type={}", request.messageId, request.brandMessageType)

        val result = try {
            retry.executeSuspendFunction {
                brandMessageProvider.send(request)
            }
        } catch (e: Exception) {
            log.error("Failed after retries: messageId={}, error={}", request.messageId, e.message)
            KakaoSendResult.fail("MAX_RETRY_EXCEEDED", e.message ?: "Exceeded maximum retry attempts")
        }

        // 리포트 발행
        val report = Report(
            messageId = request.messageId,
            code = result.resultCode ?: "UNKNOWN"
        )
        reportPublisher.publish(report)
        log.info("Report published: messageId={}, code={}", request.messageId, report.code)

        return result
    }
}
