package com.messaging.platform.skt.config

/**
 * SKT API 상수
 */
object SktApi {
    // API 경로
    const val SMS_SEND_PATH = "/sms/send"
    const val MMS_SEND_PATH = "/mms/send"

    // HTTP 헤더
    const val HEADER_API_KEY = "X-API-Key"
}

/**
 * SKT API 에러 코드
 */
enum class SktErrorCode(
    val code: String,
    val description: String,
    val retryable: Boolean
) {
    // 영구 실패
    INVALID_PARAMETER("1001", "잘못된 파라미터", false),
    INVALID_PHONE("1002", "잘못된 수신번호", false),
    INVALID_CALLBACK("1003", "잘못된 발신번호", false),

    // 일시적 오류
    SYSTEM_ERROR("5001", "시스템 오류", true),
    TIMEOUT("5002", "처리 타임아웃", true);

    companion object {
        private val codeMap = entries.associateBy { it.code }

        fun fromCode(code: String): SktErrorCode? = codeMap[code]

        fun isRetryable(code: String): Boolean = codeMap[code]?.retryable ?: false
    }
}
