package com.messaging.core.rcs.domain

/**
 * RCS 카드
 */
data class RcsCard(
    val title: String,
    val description: String? = null,
    val mediaUrl: String? = null,
    val mediaType: RcsMediaType = RcsMediaType.IMAGE,
    val buttons: List<RcsButton> = emptyList()
)

/**
 * RCS 버튼
 */
data class RcsButton(
    val type: RcsButtonType,
    val text: String,
    val url: String? = null,
    val phoneNumber: String? = null,
    val payload: String? = null
)

enum class RcsButtonType {
    URL,            // 웹 링크
    DIAL,           // 전화 걸기
    MAP,            // 지도
    CALENDAR,       // 캘린더
    POSTBACK        // 포스트백
}

enum class RcsMediaType {
    IMAGE,
    VIDEO
}
