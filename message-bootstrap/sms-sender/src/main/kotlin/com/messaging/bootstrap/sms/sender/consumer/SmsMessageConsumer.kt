package com.messaging.bootstrap.sms.sender.consumer

import com.messaging.core.sms.domain.*
import com.messaging.infrastructure.rabbitmq.config.QueueConstants
import com.messaging.infrastructure.rabbitmq.message.MessageEnvelope
import com.messaging.infrastructure.rabbitmq.message.ResultEnvelope
import com.messaging.infrastructure.rabbitmq.publisher.ResultPublisher
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

/**
 * SMS 메시지 소비자 (Spring AMQP)
 * RabbitMQ에서 SMS 발송 요청을 수신하여 처리
 */
@Component
class SmsMessageConsumer(
    private val smsProviders: List<SmsProvider>,
    private val resultPublisher: ResultPublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = [QueueConstants.SMS_SEND_QUEUE])
    fun onMessage(envelope: MessageEnvelope) {
        log.info("Received SMS message: messageId={}", envelope.messageId)

        runBlocking {
            try {
                val request = SmsSendRequest(
                    messageId = envelope.messageId,
                    type = SmsType.valueOf(envelope.type),
                    carrier = envelope.carrier?.let { Carrier.valueOf(it) },
                    recipient = envelope.recipient,
                    callback = envelope.detail["callback"] as? String ?: SmsDefaults.DEFAULT_CALLBACK,
                    content = envelope.content,
                    subject = envelope.detail["subject"] as? String,
                    imageUrl = envelope.detail["imageUrl"] as? String
                )

                val provider = selectProvider(request)
                val result = if (provider != null) {
                    provider.send(request)
                } else {
                    SmsSendResult.fail("NO_PROVIDER", "No available provider")
                }

                // 결과 발행
                val resultEnvelope = ResultEnvelope(
                    messageId = envelope.messageId,
                    partnerId = envelope.partnerId,
                    clientMsgId = envelope.clientMsgId,
                    status = if (result.success) "SUCCESS" else "FAILED",
                    resultCode = result.resultCode,
                    resultMessage = result.resultMessage
                )
                resultPublisher.publish(resultEnvelope)

                log.info("SMS processed: messageId={}, success={}", envelope.messageId, result.success)
            } catch (e: Exception) {
                log.error("Failed to process SMS: messageId={}, error={}", envelope.messageId, e.message, e)
                throw e  // 예외 던지면 NACK (DLQ로)
            }
        }
    }

    private fun selectProvider(request: SmsSendRequest): SmsProvider? {
        // 통신사 지정된 경우
        if (request.carrier != null) {
            return smsProviders.find {
                it.supportedCarrier() == request.carrier &&
                it.supportedTypes().contains(request.type)
            }
        }
        // 통신사 무관한 provider 또는 아무거나
        return smsProviders.find {
            it.supportedTypes().contains(request.type)
        }
    }
}
