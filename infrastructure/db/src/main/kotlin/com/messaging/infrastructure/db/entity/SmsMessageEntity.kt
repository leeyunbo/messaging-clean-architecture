package com.messaging.infrastructure.db.entity

import com.messaging.core.sms.domain.*
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * SMS 메시지 Entity
 */
@Table("sms_messages")
data class SmsMessageEntity(
    @Id
    val id: Long? = null,

    @Column("message_id")
    val messageId: String,

    @Column("partner_id")
    val partnerId: String,

    @Column("client_msg_id")
    val clientMsgId: String? = null,

    @Column("type")
    val type: String,

    @Column("carrier")
    val carrier: String? = null,

    @Column("recipient")
    val recipient: String,

    @Column("callback")
    val callback: String,

    @Column("content")
    val content: String,

    @Column("subject")
    val subject: String? = null,

    @Column("image_url")
    val imageUrl: String? = null,

    @Column("status")
    val status: String = "PENDING",

    @Column("retry_count")
    val retryCount: Int = 0,

    @Column("result_code")
    val resultCode: String? = null,

    @Column("result_message")
    val resultMessage: String? = null,

    @Column("sent_at")
    val sentAt: LocalDateTime? = null,

    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column("updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDomain(): SmsMessage = SmsMessage(
        id = id,
        messageId = messageId,
        partnerId = partnerId,
        clientMsgId = clientMsgId,
        type = SmsType.valueOf(type),
        carrier = carrier?.let { Carrier.valueOf(it) },
        recipient = recipient,
        callback = callback,
        content = content,
        subject = subject,
        imageUrl = imageUrl,
        status = SmsStatus.valueOf(status),
        retryCount = retryCount,
        resultCode = resultCode,
        resultMessage = resultMessage,
        sentAt = sentAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(message: SmsMessage): SmsMessageEntity = SmsMessageEntity(
            id = message.id,
            messageId = message.messageId,
            partnerId = message.partnerId,
            clientMsgId = message.clientMsgId,
            type = message.type.name,
            carrier = message.carrier?.name,
            recipient = message.recipient,
            callback = message.callback,
            content = message.content,
            subject = message.subject,
            imageUrl = message.imageUrl,
            status = message.status.name,
            retryCount = message.retryCount,
            resultCode = message.resultCode,
            resultMessage = message.resultMessage,
            sentAt = message.sentAt,
            createdAt = message.createdAt,
            updatedAt = message.updatedAt
        )
    }
}
