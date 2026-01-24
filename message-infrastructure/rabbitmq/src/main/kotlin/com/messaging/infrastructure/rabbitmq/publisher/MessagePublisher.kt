package com.messaging.infrastructure.rabbitmq.publisher

import com.messaging.infrastructure.rabbitmq.config.QueueConstants
import com.messaging.infrastructure.rabbitmq.message.MessageEnvelope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

/**
 * 메시지 발행자 (Spring AMQP)
 * Receiver에서 Sender로 메시지 전달
 */
@Component
class MessagePublisher(
    private val rabbitTemplate: RabbitTemplate
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun publish(envelope: MessageEnvelope, routingKey: String) {
        withContext(Dispatchers.IO) {
            rabbitTemplate.convertAndSend(
                QueueConstants.MESSAGE_EXCHANGE,
                routingKey,
                envelope
            )
        }
        log.debug("Message published: messageId={}, routingKey={}", envelope.messageId, routingKey)
    }

    suspend fun publishSms(envelope: MessageEnvelope) =
        publish(envelope, QueueConstants.SMS_ROUTING_KEY)

    suspend fun publishLmsMms(envelope: MessageEnvelope) =
        publish(envelope, QueueConstants.LMS_MMS_ROUTING_KEY)

    suspend fun publishRcs(envelope: MessageEnvelope) =
        publish(envelope, QueueConstants.RCS_ROUTING_KEY)

    suspend fun publishKakao(envelope: MessageEnvelope) =
        publish(envelope, QueueConstants.KAKAO_ROUTING_KEY)

    suspend fun publishNaver(envelope: MessageEnvelope) =
        publish(envelope, QueueConstants.NAVER_ROUTING_KEY)
}
