package com.messaging.core.rcs.domain

/**
 * RCS 메시지 큐 발행 Port
 */
interface RcsMessagePublisher {
    suspend fun publish(message: RcsQueueMessage)
}

data class RcsQueueMessage(
    val messageId: String,
    val partnerId: String,
    val type: String,
    val recipient: String,
    val content: String? = null,
    val buttons: List<RcsButton> = emptyList(),
    val cards: List<RcsCard> = emptyList()
)
