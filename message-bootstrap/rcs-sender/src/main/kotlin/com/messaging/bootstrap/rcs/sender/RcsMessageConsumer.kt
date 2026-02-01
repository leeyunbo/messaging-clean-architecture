package com.messaging.bootstrap.rcs.sender

import com.messaging.core.rcs.domain.RcsButton
import com.messaging.core.rcs.domain.RcsButtonType
import com.messaging.core.rcs.domain.RcsCard
import com.messaging.core.rcs.domain.RcsCarouselRequest
import com.messaging.core.rcs.domain.RcsMediaType
import com.messaging.core.rcs.domain.RcsSendRequest
import com.messaging.core.rcs.domain.RcsStandaloneRequest
import com.messaging.usecase.rcs.RcsSendUseCase
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class RcsMessageConsumer(
    private val rcsSendUseCase: RcsSendUseCase
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = ["\${rabbitmq.queue}"])
    fun onMessage(message: RcsMessage) {
        log.info("Received RCS message: messageId={}, type={}", message.messageId, message.type)

        runBlocking {
            try {
                val request = message.toRequest()
                val result = rcsSendUseCase.send(request)
                log.info("Send completed: messageId={}, success={}", message.messageId, result.success)
            } catch (e: Exception) {
                log.error("Failed to process message: messageId={}, error={}", message.messageId, e.message, e)
            }
        }
    }

    private fun RcsMessage.toRequest(): RcsSendRequest {
        return when (type) {
            RcsMessageType.STANDALONE -> RcsStandaloneRequest(
                messageId = messageId,
                recipient = recipient,
                content = content ?: throw IllegalArgumentException("Content is required for standalone message"),
                buttons = buttons.map { it.toButton() }
            )
            RcsMessageType.CAROUSEL -> RcsCarouselRequest(
                messageId = messageId,
                recipient = recipient,
                cards = cards.map { it.toCard() }
            )
        }
    }

    private fun RcsButtonDto.toButton() = RcsButton(
        type = RcsButtonType.valueOf(type),
        text = text,
        url = url,
        phoneNumber = phoneNumber,
        payload = payload
    )

    private fun RcsCardDto.toCard() = RcsCard(
        title = title,
        description = description,
        mediaUrl = mediaUrl,
        mediaType = mediaType?.let { RcsMediaType.valueOf(it) } ?: RcsMediaType.IMAGE,
        buttons = buttons.map { it.toButton() }
    )
}

enum class RcsMessageType {
    STANDALONE,
    CAROUSEL
}

data class RcsMessage(
    val messageId: String,
    val recipient: String,
    val type: RcsMessageType,
    val content: String? = null,
    val buttons: List<RcsButtonDto> = emptyList(),
    val cards: List<RcsCardDto> = emptyList()
)

data class RcsButtonDto(
    val type: String,
    val text: String,
    val url: String? = null,
    val phoneNumber: String? = null,
    val payload: String? = null
)

data class RcsCardDto(
    val title: String,
    val description: String? = null,
    val mediaUrl: String? = null,
    val mediaType: String? = null,
    val buttons: List<RcsButtonDto> = emptyList()
)
