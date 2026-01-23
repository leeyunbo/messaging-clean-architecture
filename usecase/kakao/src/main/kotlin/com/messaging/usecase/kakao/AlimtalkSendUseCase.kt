package com.messaging.usecase.kakao

import com.messaging.core.kakao.domain.AlimtalkPollingStore
import com.messaging.core.kakao.domain.AlimtalkProvider
import com.messaging.core.kakao.domain.AlimtalkRequest
import com.messaging.core.kakao.domain.KakaoSendResult
import com.messaging.core.report.domain.ReportPublisher
import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration

/**
 * 카카오 알림톡 발송 유스케이스
 *
 * ResponseMethod에 따른 처리
 * - PUSH/REALTIME: SyncResponseHandler → 리포트 발행
 * - POLLING: PollingResponseHandler → 성공 시 폴링 저장, 실패 시 리포트 발행
 */
@Service
class AlimtalkSendUseCase(
    private val alimtalkProvider: AlimtalkProvider,
    reportPublisher: ReportPublisher,
    pollingStore: AlimtalkPollingStore
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val handlerFactory = ResponseHandlerFactory(reportPublisher, pollingStore)

    private val retry = Retry.of("kakao-alimtalk-send", RetryConfig.custom<KakaoSendResult>()
        .maxAttempts(3)
        .waitDuration(Duration.ofSeconds(1))
        .retryOnResult { it.retryable }
        .failAfterMaxAttempts(true)
        .build())

    suspend fun send(request: AlimtalkRequest): KakaoSendResult {
        log.info("Processing Alimtalk send: messageId={}, responseMethod={}",
            request.messageId, request.responseMethod)

        val result = executeWithRetry(request)
        val handler = handlerFactory.getHandler(request.responseMethod)
        handler.handle(request.messageId, result)

        return result
    }

    private suspend fun executeWithRetry(request: AlimtalkRequest): KakaoSendResult {
        return try {
            retry.executeSuspendFunction {
                alimtalkProvider.send(request)
            }
        } catch (e: Exception) {
            log.error("Failed after retries: messageId={}, error={}", request.messageId, e.message)
            KakaoSendResult.fail("MAX_RETRY_EXCEEDED", e.message ?: "Exceeded maximum retry attempts")
        }
    }
}
