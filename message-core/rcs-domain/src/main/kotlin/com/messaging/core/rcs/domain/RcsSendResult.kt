package com.messaging.core.rcs.domain

data class RcsSendResult(
    val success: Boolean,
    val resultCode: String? = null,
    val resultMessage: String? = null,
    val requestId: String? = null
) {
    companion object {
        fun success(requestId: String? = null) = RcsSendResult(
            success = true,
            requestId = requestId
        )

        fun fail(code: String, message: String) = RcsSendResult(
            success = false,
            resultCode = code,
            resultMessage = message
        )

        fun unknownError() = RcsSendResult(
            success = false,
            resultCode = "9999",
            resultMessage = "UNKNOWN_ERROR"
        )

        fun rcsApiError() = RcsSendResult(
            success = false,
            resultCode = "9998",
            resultMessage = "RCS_API_ERROR"
        )
    }
}
