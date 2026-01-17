package com.messaging.provider

/**
 * Provider 발송 결과
 * - 벤더 API 호출 결과만 담당
 */
data class SendResult(
    val success: Boolean,
    val resultCode: String,
    val resultMessage: String,
    val retryable: Boolean = false
) {
    companion object {
        fun success(resultMessage: String = "성공") = SendResult(
            success = true,
            resultCode = "0000",
            resultMessage = resultMessage
        )

        fun fail(resultCode: String, resultMessage: String, retryable: Boolean = false) = SendResult(
            success = false,
            resultCode = resultCode,
            resultMessage = resultMessage,
            retryable = retryable
        )
    }
}
