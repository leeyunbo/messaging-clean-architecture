package com.messaging.core.rcs.domain

import java.time.LocalDateTime

/**
 * RCS 메시지 도메인 객체
 */
data class RcsMessage(
    val id: Long? = null,
    val messageId: String,
    val partnerId: String,
    val clientMsgId: String? = null,
    val type: RcsMessageType,
    val recipient: String,
    val content: String? = null,
    val cards: List<RcsCard> = emptyList(),
    val buttons: List<RcsButton> = emptyList(),
    val status: RcsStatus = RcsStatus.PENDING,
    val retryCount: Int = 0,
    val resultCode: String? = null,
    val resultMessage: String? = null,
    val sentAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * RCS 메시지 타입
 */
enum class RcsMessageType {
    STANDALONE,     // 단일 메시지
    CAROUSEL        // 캐러셀 (여러 카드)
}

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

/**
 * RCS 메시지 상태
 */
enum class RcsStatus {
    PENDING,
    SENDING,
    SUCCESS,
    FAILED,
    EXPIRED
}
