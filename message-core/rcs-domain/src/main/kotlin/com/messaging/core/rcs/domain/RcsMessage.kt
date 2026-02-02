package com.messaging.core.rcs.domain

/**
 * RCS 수신 메시지 도메인 모델
 * Sealed class로 타입별 필수 필드를 컴파일 타임에 강제
 */
sealed class RcsReceiveMessage {
    abstract val partnerId: String
    abstract val recipient: PhoneNumber

    /**
     * 단일 메시지 (content 필수)
     */
    data class Standalone(
        override val partnerId: String,
        override val recipient: PhoneNumber,
        val content: String,
        val buttons: List<RcsButton> = emptyList()
    ) : RcsReceiveMessage() {
        init {
            require(content.isNotBlank()) { "Content must not be blank for standalone message" }
        }
    }

    /**
     * 캐러셀 메시지 (cards 필수, 1개 이상)
     */
    data class Carousel(
        override val partnerId: String,
        override val recipient: PhoneNumber,
        val cards: List<RcsCard>
    ) : RcsReceiveMessage() {
        init {
            require(cards.isNotEmpty()) { "Cards must not be empty for carousel message" }
        }
    }

    companion object {
        fun standalone(
            partnerId: String,
            recipient: PhoneNumber,
            content: String,
            buttons: List<RcsButton> = emptyList()
        ): Standalone = Standalone(partnerId, recipient, content, buttons)

        fun carousel(
            partnerId: String,
            recipient: PhoneNumber,
            cards: List<RcsCard>
        ): Carousel = Carousel(partnerId, recipient, cards)
    }
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
