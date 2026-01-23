package com.messaging.core.rcs.domain

/**
 * RCS 단일 메시지 발송 요청
 */
data class RcsStandaloneRequest(
    val messageId: String,
    val recipient: String,
    val content: String,
    val buttons: List<RcsButton> = emptyList()
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

/**
 * RCS 캐러셀 메시지 발송 요청
 */
data class RcsCarouselRequest(
    val messageId: String,
    val recipient: String,
    val cards: List<RcsCard>
) {
    init {
        require(recipient.matches(Regex("^01[0-9]{8,9}$"))) {
            "Invalid phone number format: $recipient"
        }
        require(cards.isNotEmpty()) {
            "Carousel must have at least one card"
        }
        require(cards.size <= 10) {
            "Carousel can have maximum 10 cards"
        }
    }
}
