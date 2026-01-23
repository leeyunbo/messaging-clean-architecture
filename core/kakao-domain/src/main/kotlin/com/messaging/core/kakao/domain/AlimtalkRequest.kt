package com.messaging.core.kakao.domain

/**
 * 알림톡 발송 요청
 */
data class AlimtalkRequest(
    val messageId: String,
    val responseMethod: ResponseMethod,
    val timeout: Long? = null,  // REALTIME일 때 사용 (ms)
    val variables: Map<String, Any?> = emptyMap()
)

/**
 * 응답 방식
 */
enum class ResponseMethod {
    PUSH,      // 비동기 - 발송 후 결과는 콜백으로
    REALTIME,  // 동기 - timeout 내 결과 대기
    POLLING    // 폴링 - 발송 후 주기적으로 결과 조회
}
