package com.messaging.usecase.rcs

import com.messaging.core.rcs.domain.RcsMessagePublisher
import com.messaging.core.rcs.domain.RcsQueueMessage
import com.messaging.library.idgen.MessageIdGenerator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RcsReceiveUseCase(
    private val messagePublisher: RcsMessagePublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun receive(request: RcsReceiveRequest): ReceiveResult {
        // 검증
        if (!isValidRecipient(request.recipient)) {
            return ReceiveResult.failure("4001", "Invalid recipient")
        }

        if (request.type == "STANDALONE" && request.content.isNullOrBlank()) {
            return ReceiveResult.failure("4002", "Content is required for standalone message")
        }

        if (request.type == "CAROUSEL" && request.cards.isEmpty()) {
            return ReceiveResult.failure("4003", "Cards are required for carousel message")
        }

        val messageId = MessageIdGenerator.generate()
        log.info("Receiving RCS message: messageId={}, partnerId={}, recipient={}",
            messageId, request.partnerId, request.recipient)

        val queueMessage = RcsQueueMessage(
            messageId = messageId,
            partnerId = request.partnerId,
            type = request.type,
            recipient = request.recipient,
            content = request.content,
            buttons = request.buttons,
            cards = request.cards
        )

        messagePublisher.publish(queueMessage)
        log.info("Published to queue: messageId={}", messageId)

        return ReceiveResult.success(messageId)
    }

    private fun isValidRecipient(recipient: String): Boolean {
        return recipient.matches(Regex("^01[0-9]{8,9}$"))
    }
}

data class RcsReceiveRequest(
    val partnerId: String,
    val type: String,
    val recipient: String,
    val content: String? = null,
    val buttons: List<Map<String, Any?>> = emptyList(),
    val cards: List<Map<String, Any?>> = emptyList()
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
