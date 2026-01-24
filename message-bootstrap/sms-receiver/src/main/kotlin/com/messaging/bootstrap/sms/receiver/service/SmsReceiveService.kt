package com.messaging.bootstrap.sms.receiver.service

import com.messaging.bootstrap.sms.receiver.dto.SendSmsRequest
import com.messaging.bootstrap.sms.receiver.dto.SendSmsResponse
import com.messaging.core.sms.domain.*
import com.messaging.infrastructure.rabbitmq.message.MessageEnvelope
import com.messaging.infrastructure.rabbitmq.publisher.MessagePublisher
import com.messaging.library.idgen.MessageIdGenerator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SmsReceiveService(
    private val smsRepository: SmsRepository,
    private val messagePublisher: MessagePublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun receive(partnerId: String, request: SendSmsRequest): SendSmsResponse {
        // 중복 체크
        if (request.clientMsgId != null) {
            val existing = smsRepository.findByPartnerIdAndClientMsgId(partnerId, request.clientMsgId)
            if (existing != null) {
                log.warn("Duplicate message detected: partnerId={}, clientMsgId={}", partnerId, request.clientMsgId)
                return SendSmsResponse(
                    messageId = existing.messageId,
                    status = "DUPLICATE",
                    message = "Message already exists"
                )
            }
        }

        // 메시지 ID 생성
        val messageId = MessageIdGenerator.generate()

        // 통신사 파싱
        val carrier = request.carrier?.let {
            try { Carrier.valueOf(it.uppercase()) } catch (e: Exception) { null }
        }

        // 메시지 저장
        val message = SmsMessage(
            messageId = messageId,
            partnerId = partnerId,
            clientMsgId = request.clientMsgId,
            type = SmsType.SMS,
            carrier = carrier,
            recipient = request.recipient,
            callback = request.callback ?: SmsDefaults.DEFAULT_CALLBACK,
            content = request.content,
            status = SmsStatus.PENDING
        )

        smsRepository.save(message)
        log.info("Message saved: messageId={}, partnerId={}", messageId, partnerId)

        // 큐에 발행
        val envelope = MessageEnvelope(
            messageId = messageId,
            partnerId = partnerId,
            clientMsgId = request.clientMsgId,
            type = SmsType.SMS.name,
            carrier = carrier?.name,
            recipient = request.recipient,
            content = request.content,
            detail = mapOf("callback" to (request.callback ?: SmsDefaults.DEFAULT_CALLBACK))
        )

        messagePublisher.publishSms(envelope)
        log.info("Message published to queue: messageId={}", messageId)

        return SendSmsResponse(
            messageId = messageId,
            status = "ACCEPTED",
            message = "Message accepted for delivery"
        )
    }

    suspend fun getStatus(partnerId: String, messageId: String): Map<String, Any?> {
        val message = smsRepository.findByMessageId(messageId)
            ?: throw NoSuchElementException("Message not found: $messageId")

        if (message.partnerId != partnerId) {
            throw IllegalAccessException("Access denied")
        }

        return mapOf(
            "messageId" to message.messageId,
            "status" to message.status.name,
            "resultCode" to message.resultCode,
            "resultMessage" to message.resultMessage,
            "sentAt" to message.sentAt,
            "createdAt" to message.createdAt
        )
    }
}
