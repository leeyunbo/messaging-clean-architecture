package com.messaging.core.naver.domain

import java.time.LocalDateTime

/**
 * 네이버 클라우드 메시지 도메인 객체
 */
data class NaverMessage(
    val id: Long? = null,
    val messageId: String,
    val partnerId: String,
    val clientMsgId: String? = null,
    val type: NaverMessageType,
    val recipient: String,
    val content: String,
    val subject: String? = null,              // LMS 제목
    val templateCode: String? = null,         // 알림톡용
    val plusFriendId: String? = null,         // 알림톡용
    val buttons: List<NaverButton> = emptyList(),
    val status: NaverStatus = NaverStatus.PENDING,
    val retryCount: Int = 0,
    val resultCode: String? = null,
    val resultMessage: String? = null,
    val sentAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * 네이버 메시지 타입
 */
enum class NaverMessageType {
    SMS,
    LMS,
    MMS,
    ALIMTALK    // 네이버 클라우드 알림톡
}

/**
 * 네이버 버튼 (알림톡용)
 */
data class NaverButton(
    val type: NaverButtonType,
    val name: String,
    val linkMobile: String? = null,
    val linkPc: String? = null
)

enum class NaverButtonType {
    WL,     // 웹 링크
    AL,     // 앱 링크
    BK,     // 봇 키워드
    MD      // 메시지 전달
}

/**
 * 네이버 메시지 상태
 */
enum class NaverStatus {
    PENDING,
    SENDING,
    SUCCESS,
    FAILED,
    EXPIRED
}
