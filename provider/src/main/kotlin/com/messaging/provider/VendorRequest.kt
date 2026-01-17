package com.messaging.provider

/**
 * 벤더 API 요청
 * - Provider는 messageId 필드명 매핑만 담당
 * - 나머지 detail은 그대로 전달 (passthrough)
 */
data class VendorRequest(
    val body: Map<String, Any?>,
    val headers: Map<String, Any?> = emptyMap()
)
