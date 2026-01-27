package com.messaging.core.naver.domain

data class NaverSendResult(
    val success: Boolean,
    val resultCode: String? = null,
    val resultMessage: String? = null,
    val requestId: String? = null
) {
    companion object {
        fun success(requestId: String? = null) = NaverSendResult(
            success = true,
            requestId = requestId
        )

        fun fail(code: String, message: String) = NaverSendResult(
            success = false,
            resultCode = code,
            resultMessage = message
        )

        fun unknownError() = NaverSendResult(
            success = false,
            resultCode = "9999",
            resultMessage = "UNKNOWN_ERROR"
        )

        fun naverApiError() = NaverSendResult(
            success = false,
            resultCode = "9998",
            resultMessage = "NAVER_API_ERROR"
        )
    }
}
