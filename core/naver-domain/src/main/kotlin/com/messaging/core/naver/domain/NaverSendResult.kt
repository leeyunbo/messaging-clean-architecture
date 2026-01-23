package com.messaging.core.naver.domain

/**
 * 네이버 발송 결과
 */
data class NaverSendResult(
    val success: Boolean,
    val resultCode: String?,
    val resultMessage: String?,
    val requestId: String? = null,
    val retryable: Boolean = false
) {
    companion object {
        fun success(code: String = "202", message: String = "Accepted", requestId: String? = null) = NaverSendResult(
            success = true,
            resultCode = code,
            resultMessage = message,
            requestId = requestId,
            retryable = false
        )

        fun fail(code: String, message: String) = NaverSendResult(
            success = false,
            resultCode = code,
            resultMessage = message,
            retryable = false
        )

        fun retryable(code: String, message: String) = NaverSendResult(
            success = false,
            resultCode = code,
            resultMessage = message,
            retryable = true
        )
    }
}
