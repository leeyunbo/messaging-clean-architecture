package com.messaging.core.sms.domain

/**
 * SMS 발송 결과
 */
data class SmsSendResult(
    val success: Boolean,
    val resultCode: String?,
    val resultMessage: String?,
    val retryable: Boolean = false
) {
    companion object {
        fun success(code: String = "0000", message: String = "Success") = SmsSendResult(
            success = true,
            resultCode = code,
            resultMessage = message,
            retryable = false
        )

        fun fail(code: String, message: String) = SmsSendResult(
            success = false,
            resultCode = code,
            resultMessage = message,
            retryable = false
        )

        fun retryable(code: String, message: String) = SmsSendResult(
            success = false,
            resultCode = code,
            resultMessage = message,
            retryable = true
        )
    }
}
