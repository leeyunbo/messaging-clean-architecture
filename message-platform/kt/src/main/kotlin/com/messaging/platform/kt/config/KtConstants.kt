package com.messaging.platform.kt.config

/**
 * KT API 상수
 */
object KtApi {
    // API 경로
    const val SMS_SEND_PATH = "/v1/sms/send"

    // HTTP 헤더
    const val HEADER_AUTHORIZATION = "Authorization"
    const val BEARER_PREFIX = "Bearer "
}

/**
 * KT API 에러 코드
 */
object KtErrorCode {
    // 영구 실패
    val PERMANENT_FAILURES = setOf("400", "401", "403")

    // 일시적 오류
    val RETRYABLE_FAILURES = setOf("500", "502", "503")

    fun isRetryable(code: String): Boolean = code in RETRYABLE_FAILURES
}
