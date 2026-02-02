package com.messaging.infrastructure.rabbitmq

import com.messaging.core.rcs.domain.RcsMessagePublisher
import com.messaging.core.rcs.domain.RcsQueueMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RabbitMqRcsMessagePublisher(
    private val rabbitTemplate: RabbitTemplate,
    @param:Value("\${rabbitmq.queue:rcs-send-queue}") private val queueName: String
) : RcsMessagePublisher {
    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun publish(message: RcsQueueMessage) {
        withContext(Dispatchers.IO) {
            rabbitTemplate.convertAndSend(queueName, message)
        }
        log.debug("RCS message published to queue: messageId={}", message.messageId)
    }
}
