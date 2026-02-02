package com.messaging.usecase.rcs

import com.messaging.core.rcs.domain.PhoneNumber
import com.messaging.core.rcs.domain.RcsButton
import com.messaging.core.rcs.domain.RcsCard
import com.messaging.core.rcs.domain.RcsMessagePublisher
import com.messaging.core.rcs.domain.RcsQueueMessage
import com.messaging.core.rcs.domain.RcsReceiveMessage
import com.messaging.library.idgen.MessageIdGenerator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RcsReceiveUseCase(
    private val messagePublisher: RcsMessagePublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun receive(request: RcsReceiveRequest): ReceiveResult {
        val message = try {
            request.toDomainModel()
        } catch (e: IllegalArgumentException) {
            return ReceiveResult.failure("4001", e.message ?: "Invalid request")
        }

        val messageId = MessageIdGenerator.generate()
        log.info("Receiving RCS message: messageId={}, partnerId={}, recipient={}",
            messageId, message.partnerId, message.recipient.value)

        val queueMessage = message.toQueueMessage(messageId)

        messagePublisher.publish(queueMessage)
        log.info("Published to queue: messageId={}", messageId)

        return ReceiveResult.success(messageId)
    }
}

data class RcsReceiveRequest(
    val partnerId: String,
    val type: String,
    val recipient: String,
    val content: String? = null,
    val buttons: List<RcsButton> = emptyList(),
    val cards: List<RcsCard> = emptyList()
) {
    fun toDomainModel(): RcsReceiveMessage {
        val phoneNumber = PhoneNumber.of(recipient)

        return when (type.uppercase()) {
            "STANDALONE" -> RcsReceiveMessage.standalone(
                partnerId = partnerId,
                recipient = phoneNumber,
                content = content ?: throw IllegalArgumentException("Content is required for standalone message"),
                buttons = buttons
            )
            "CAROUSEL" -> RcsReceiveMessage.carousel(
                partnerId = partnerId,
                recipient = phoneNumber,
                cards = cards
            )
            else -> throw IllegalArgumentException("Unknown message type: $type")
        }
    }
}

private fun RcsReceiveMessage.toQueueMessage(messageId: String): RcsQueueMessage {
    return when (this) {
        is RcsReceiveMessage.Standalone -> RcsQueueMessage(
            messageId = messageId,
            partnerId = partnerId,
            type = "STANDALONE",
            recipient = recipient.value,
            content = content,
            buttons = buttons.map { it.toMap() }
        )
        is RcsReceiveMessage.Carousel -> RcsQueueMessage(
            messageId = messageId,
            partnerId = partnerId,
            type = "CAROUSEL",
            recipient = recipient.value,
            cards = cards.map { it.toMap() }
        )
    }
}

private fun RcsButton.toMap(): Map<String, Any?> = mapOf(
    "type" to type.name,
    "text" to text,
    "url" to url,
    "phoneNumber" to phoneNumber,
    "payload" to payload
)

private fun RcsCard.toMap(): Map<String, Any?> = mapOf(
    "title" to title,
    "description" to description,
    "mediaUrl" to mediaUrl,
    "mediaType" to mediaType.name,
    "buttons" to buttons.map { it.toMap() }
)

data class ReceiveResult(
    val success: Boolean,
    val messageId: String? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null
) {
    companion object {
        fun success(messageId: String) = ReceiveResult(success = true, messageId = messageId)
        fun failure(code: String, message: String) = ReceiveResult(
            success = false,
            errorCode = code,
            errorMessage = message
        )
    }
}
