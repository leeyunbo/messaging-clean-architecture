package com.messaging.usecase.sms

import com.messaging.core.sms.domain.*
import io.github.resilience4j.kotlin.retry.executeSuspendFunction
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration

/**
 * SMS 발송 유스케이스
 */
@Service
class SmsSendUseCase(
    private val providerRouter: SmsProviderRouter,
    private val smsRepository: SmsRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val retry = Retry.of("sms-send", RetryConfig.custom<SmsSendResult>()
        .maxAttempts(3)
        .waitDuration(Duration.ofSeconds(1))
        .retryOnResult { it.retryable }
        .failAfterMaxAttempts(true)
        .build())

    suspend fun send(request: SmsSendRequest): SmsSendResult {
        log.info("Processing SMS send: messageId={}, recipient={}", request.messageId, request.recipient)

        smsRepository.updateStatus(request.messageId, SmsStatus.SENDING)

        val provider = providerRouter.route(request)
        if (provider == null) {
            log.error("No provider found: messageId={}", request.messageId)
            val result = SmsSendResult.fail("NO_PROVIDER", "No available provider")
            saveResult(request.messageId, result)
            return result
        }

        val result = try {
            retry.executeSuspendFunction {
                provider.send(request)
            }
        } catch (e: Exception) {
            log.error("Failed after retries: messageId={}, error={}", request.messageId, e.message)
            SmsSendResult.fail("MAX_RETRY_EXCEEDED", e.message ?: "Exceeded maximum retry attempts")
        }

        saveResult(request.messageId, result)
        return result
    }

    private suspend fun saveResult(messageId: String, result: SmsSendResult) {
        val status = if (result.success) SmsStatus.SUCCESS else SmsStatus.FAILED
        smsRepository.updateResult(
            messageId = messageId,
            status = status,
            resultCode = result.resultCode ?: "UNKNOWN",
            resultMessage = result.resultMessage ?: ""
        )
    }
}
