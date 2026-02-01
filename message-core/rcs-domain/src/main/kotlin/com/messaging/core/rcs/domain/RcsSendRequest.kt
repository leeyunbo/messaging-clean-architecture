package com.messaging.core.rcs.domain

sealed class RcsSendRequest {
    abstract val messageId: String
    abstract val recipient: String
}

data class RcsStandaloneRequest(
    override val messageId: String,
    override val recipient: String,
    val content: String,
    val buttons: List<RcsButton> = emptyList()
) : RcsSendRequest() {
    init {
        require(recipient.matches(Regex("^01[0-9]{8,9}$"))) {
            "Invalid phone number format: $recipient"
        }
        require(content.isNotBlank()) {
            "Content must not be blank"
        }
    }
}

data class RcsCarouselRequest(
    override val messageId: String,
    override val recipient: String,
    val cards: List<RcsCard>
) : RcsSendRequest() {
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
