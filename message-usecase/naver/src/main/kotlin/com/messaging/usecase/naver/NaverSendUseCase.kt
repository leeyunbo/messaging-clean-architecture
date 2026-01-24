package com.messaging.usecase.naver

import com.messaging.core.naver.domain.*
import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration

/**
 * 네이버 SMS 발송 유스케이스
 */
@Service
class NaverSmsSendUseCase(
    private val naverSmsProvider: NaverSmsProvider,
    private val naverRepository: NaverRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val retry = Retry.of("naver-sms-send", RetryConfig.custom<NaverSendResult>()
        .maxAttempts(3)
        .waitDuration(Duration.ofSeconds(1))
        .retryOnResult { it.retryable }
        .failAfterMaxAttempts(true)
        .build())

    suspend fun send(request: NaverSmsRequest): NaverSendResult {
        log.info("Processing Naver SMS send: messageId={}, recipient={}", request.messageId, request.recipient)

        naverRepository.updateStatus(request.messageId, NaverStatus.SENDING)

        val result = try {
            retry.executeSuspendFunction {
                naverSmsProvider.send(request)
            }
        } catch (e: Exception) {
            log.error("Failed after retries: messageId={}, error={}", request.messageId, e.message)
            NaverSendResult.fail("MAX_RETRY_EXCEEDED", e.message ?: "Exceeded maximum retry attempts")
        }

        saveResult(request.messageId, result)
        return result
    }

    private suspend fun saveResult(messageId: String, result: NaverSendResult) {
        val status = if (result.success) NaverStatus.SUCCESS else NaverStatus.FAILED
        naverRepository.updateResult(
            messageId = messageId,
            status = status,
            resultCode = result.resultCode ?: "UNKNOWN",
            resultMessage = result.resultMessage ?: ""
        )
    }
}

/**
 * 네이버 알림톡 발송 유스케이스
 */
@Service
class NaverAlimtalkSendUseCase(
    private val naverAlimtalkProvider: NaverAlimtalkProvider,
    private val naverRepository: NaverRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val retry = Retry.of("naver-alimtalk-send", RetryConfig.custom<NaverSendResult>()
        .maxAttempts(3)
        .waitDuration(Duration.ofSeconds(1))
        .retryOnResult { it.retryable }
        .failAfterMaxAttempts(true)
        .build())

    suspend fun send(request: NaverAlimtalkRequest): NaverSendResult {
        log.info("Processing Naver Alimtalk send: messageId={}, recipient={}", request.messageId, request.recipient)

        naverRepository.updateStatus(request.messageId, NaverStatus.SENDING)

        val result = try {
            retry.executeSuspendFunction {
                naverAlimtalkProvider.send(request)
            }
        } catch (e: Exception) {
            log.error("Failed after retries: messageId={}, error={}", request.messageId, e.message)
            NaverSendResult.fail("MAX_RETRY_EXCEEDED", e.message ?: "Exceeded maximum retry attempts")
        }

        saveResult(request.messageId, result)
        return result
    }

    private suspend fun saveResult(messageId: String, result: NaverSendResult) {
        val status = if (result.success) NaverStatus.SUCCESS else NaverStatus.FAILED
        naverRepository.updateResult(
            messageId = messageId,
            status = status,
            resultCode = result.resultCode ?: "UNKNOWN",
            resultMessage = result.resultMessage ?: ""
        )
    }
}
