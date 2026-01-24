package com.messaging.core.kakao.domain

/**
 * 브랜드메시지 발송 요청 (Kakao Direct)
 * - messageId: 우리 시스템 메시지 ID
 * - brandMessageType: URI path 결정에 사용
 * - variables: 카카오에 그대로 전달되는 데이터
 */
data class BrandMessageRequest(
    val messageId: String,
    val brandMessageType: BrandMessageType,
    val variables: Map<String, Any?> = emptyMap()
)

/**
 * 브랜드메시지 타입 (URI path 결정)
 */
enum class BrandMessageType(val path: String) {
    BASIC("/v1/basic"),
    FREE("/v1/free"),
    GROUP_MESSAGE("/v1/broadcast")
}
