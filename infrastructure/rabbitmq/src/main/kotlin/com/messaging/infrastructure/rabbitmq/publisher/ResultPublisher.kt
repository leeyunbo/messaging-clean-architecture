package com.messaging.infrastructure.rabbitmq.publisher

import com.messaging.infrastructure.rabbitmq.config.QueueConstants
import com.messaging.infrastructure.rabbitmq.message.ResultEnvelope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

/**
 * 결과 발행자 (Spring AMQP)
 * Sender에서 Reporter로 발송 결과 전달
 */
@Component
class ResultPublisher(
    private val rabbitTemplate: RabbitTemplate
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun publish(envelope: ResultEnvelope) {
        withContext(Dispatchers.IO) {
            rabbitTemplate.convertAndSend(
                QueueConstants.RESULT_EXCHANGE,
                QueueConstants.RESULT_ROUTING_KEY,
                envelope
            )
        }
        log.debug("Result published: messageId={}, status={}", envelope.messageId, envelope.status)
    }
}
