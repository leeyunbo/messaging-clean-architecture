package com.messaging.usecase.rcs

import com.messaging.core.rcs.domain.PhoneNumber
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
        val phoneNumber = try {
            PhoneNumber.of(request.recipient)
        } catch (e: IllegalArgumentException) {
            return ReceiveResult.failure("4001", e.message ?: "Invalid phone number")
        }

        val messageId = MessageIdGenerator.generate()
        log.info("Receiving RCS message: messageId={}, partnerId={}, recipient={}",
            messageId, request.partnerId, phoneNumber.value)

        val queueMessage = RcsQueueMessage(
            messageId = messageId,
            partnerId = request.partnerId,
            type = request.type,
            recipient = phoneNumber.value,
            content = request.content,
            buttons = request.buttons,
            cards = request.cards
        )

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
    val buttons: String? = null,
    val cards: String? = null
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
