package com.messaging.core.kakao.domain

/**
 * 카카오 발송 결과
 */
data class KakaoSendResult(
    val success: Boolean,
    val resultCode: String?,
    val resultMessage: String?,
    val serialNumber: String? = null,  // 폴링 시 필요
    val retryable: Boolean = false
) {
    companion object {
        fun success(code: String = "0", message: String = "Success", serialNumber: String? = null) = KakaoSendResult(
            success = true,
            resultCode = code,
            resultMessage = message,
            serialNumber = serialNumber,
            retryable = false
        )

        fun fail(code: String, message: String) = KakaoSendResult(
            success = false,
            resultCode = code,
            resultMessage = message,
            retryable = false
        )

        fun retryable(code: String, message: String) = KakaoSendResult(
            success = false,
            resultCode = code,
            resultMessage = message,
            retryable = true
        )
    }
}
