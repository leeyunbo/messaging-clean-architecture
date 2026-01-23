package com.messaging.platform.naver.config

/**
 * 네이버 클라우드 API 상수
 */
object NaverApi {
    // API 경로 템플릿
    const val SMS_SEND_PATH_TEMPLATE = "/sms/v2/services/%s/messages"
    const val ALIMTALK_SEND_PATH_TEMPLATE = "/alimtalk/v2/services/%s/messages"

    // HTTP 헤더
    const val HEADER_TIMESTAMP = "x-ncp-apigw-timestamp"
    const val HEADER_ACCESS_KEY = "x-ncp-iam-access-key"
    const val HEADER_SIGNATURE = "x-ncp-apigw-signature-v2"

    // 서명 알고리즘
    const val SIGNATURE_ALGORITHM = "HmacSHA256"
}

/**
 * 네이버 클라우드 API 에러 코드
 */
object NaverErrorCode {
    // 영구 실패 (재시도 불필요)
    val PERMANENT_FAILURES = setOf("400", "401", "403", "404")

    // 일시적 오류 (재시도 가능)
    val RETRYABLE_FAILURES = setOf("500", "502", "503")

    fun isRetryable(code: String): Boolean = code in RETRYABLE_FAILURES
}
