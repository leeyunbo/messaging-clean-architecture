package com.messaging.platform.kakao.config

/**
 * 카카오 알림톡 API 상수
 */
object KakaoAlimtalkApi {
    // API 경로
    const val SEND_PATH = "/alimtalk/v2/send"

    // HTTP 헤더
    const val HEADER_API_KEY = "X-Api-Key"
}

/**
 * 카카오 API 에러 코드
 * @see <a href="https://developers.kakao.com/docs/latest/ko/message/common">카카오 메시지 API 문서</a>
 */
enum class KakaoErrorCode(
    val code: Int,
    val description: String,
    val retryable: Boolean
) {
    // 영구 실패 (재시도 불필요)
    INVALID_PARAMETER(-1000, "잘못된 파라미터", false),
    INVALID_TEMPLATE(-1001, "템플릿 불일치", false),
    INVALID_PHONE(-1002, "잘못된 수신번호", false),

    // 일시적 오류 (재시도 가능)
    SYSTEM_ERROR(-9000, "카카오 시스템 오류", true),
    TIMEOUT(-9001, "카카오 처리 타임아웃", true);

    companion object {
        private val codeMap = entries.associateBy { it.code }

        fun fromCode(code: Int): KakaoErrorCode? = codeMap[code]

        fun isRetryable(code: Int): Boolean = codeMap[code]?.retryable ?: false
    }
}
