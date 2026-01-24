package com.messaging.usecase.rcs

import com.messaging.core.rcs.domain.*
import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration

/**
 * RCS 발송 유스케이스
 */
@Service
class RcsSendUseCase(
    private val rcsProvider: RcsProvider,
    private val rcsRepository: RcsRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val retry = Retry.of("rcs-send", RetryConfig.custom<RcsSendResult>()
        .maxAttempts(3)
        .waitDuration(Duration.ofSeconds(1))
        .retryOnResult { it.retryable }
        .failAfterMaxAttempts(true)
        .build())

    suspend fun sendStandalone(request: RcsStandaloneRequest): RcsSendResult {
        log.info("Processing RCS Standalone send: messageId={}, recipient={}", request.messageId, request.recipient)

        rcsRepository.updateStatus(request.messageId, RcsStatus.SENDING)

        val result = try {
            retry.executeSuspendFunction {
                rcsProvider.sendStandalone(request)
            }
        } catch (e: Exception) {
            log.error("Failed after retries: messageId={}, error={}", request.messageId, e.message)
            RcsSendResult.fail("MAX_RETRY_EXCEEDED", e.message ?: "Exceeded maximum retry attempts")
        }

        saveResult(request.messageId, result)
        return result
    }

    suspend fun sendCarousel(request: RcsCarouselRequest): RcsSendResult {
        log.info("Processing RCS Carousel send: messageId={}, recipient={}", request.messageId, request.recipient)

        rcsRepository.updateStatus(request.messageId, RcsStatus.SENDING)

        val result = try {
            retry.executeSuspendFunction {
                rcsProvider.sendCarousel(request)
            }
        } catch (e: Exception) {
            log.error("Failed after retries: messageId={}, error={}", request.messageId, e.message)
            RcsSendResult.fail("MAX_RETRY_EXCEEDED", e.message ?: "Exceeded maximum retry attempts")
        }

        saveResult(request.messageId, result)
        return result
    }

    private suspend fun saveResult(messageId: String, result: RcsSendResult) {
        val status = if (result.success) RcsStatus.SUCCESS else RcsStatus.FAILED
        rcsRepository.updateResult(
            messageId = messageId,
            status = status,
            resultCode = result.resultCode ?: "UNKNOWN",
            resultMessage = result.resultMessage ?: ""
        )
    }
}
