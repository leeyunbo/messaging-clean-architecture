package com.messaging.core.naver.domain

/**
 * 네이버 SMS 발송 요청
 */
data class NaverSmsRequest(
    val messageId: String,
    val type: NaverSmsType,
    val recipient: String,
    val callback: String,
    val content: String,
    val subject: String? = null     // LMS용
) {
    init {
        require(recipient.matches(Regex("^01[0-9]{8,9}$"))) {
            "Invalid phone number format: $recipient"
        }
        require(content.isNotBlank()) {
            "Content must not be blank"
        }
    }
}

enum class NaverSmsType {
    SMS,
    LMS,
    MMS
}

/**
 * 네이버 알림톡 발송 요청
 */
data class NaverAlimtalkRequest(
    val messageId: String,
    val recipient: String,
    val templateCode: String,           // 필수
    val plusFriendId: String,           // 필수
    val content: String,
    val buttons: List<NaverButton> = emptyList()
) {
    init {
        require(recipient.matches(Regex("^01[0-9]{8,9}$"))) {
            "Invalid phone number format: $recipient"
        }
        require(templateCode.isNotBlank()) {
            "templateCode is required"
        }
        require(plusFriendId.isNotBlank()) {
            "plusFriendId is required"
        }
    }
}
