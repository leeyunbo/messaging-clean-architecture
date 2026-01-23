package com.messaging.core.rcs.domain

/**
 * RCS 발송 결과
 */
data class RcsSendResult(
    val success: Boolean,
    val resultCode: String?,
    val resultMessage: String?,
    val retryable: Boolean = false
) {
    companion object {
        fun success(code: String = "0000", message: String = "Success") = RcsSendResult(
            success = true,
            resultCode = code,
            resultMessage = message,
            retryable = false
        )

        fun fail(code: String, message: String) = RcsSendResult(
            success = false,
            resultCode = code,
            resultMessage = message,
            retryable = false
        )

        fun retryable(code: String, message: String) = RcsSendResult(
            success = false,
            resultCode = code,
            resultMessage = message,
            retryable = true
        )
    }
}
