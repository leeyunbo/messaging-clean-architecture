package com.messaging.bootstrap.naver

import com.messaging.core.naver.domain.NaverSendRequest
import com.messaging.usecase.naver.NaverSendUseCase
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class NaverMessageConsumer(
    private val naverSendUseCase: NaverSendUseCase
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = ["\${rabbitmq.queue}"])
    fun onMessage(message: NaverMessage) {
        log.info("Received message: messageId={}", message.messageId)

        runBlocking {
            try {
                val request = message.toRequest()
                val result = naverSendUseCase.send(request)
                log.info("Send completed: messageId={}, success={}", message.messageId, result.success)
            } catch (e: Exception) {
                log.error("Failed to process message: messageId={}, error={}", message.messageId, e.message, e)
            }
        }
    }

    private fun NaverMessage.toRequest() = NaverSendRequest(
        messageId = messageId,
        recipient = recipient,
        content = content
    )
}

data class NaverMessage(
    val messageId: String,
    val recipient: String,
    val content: String
)
