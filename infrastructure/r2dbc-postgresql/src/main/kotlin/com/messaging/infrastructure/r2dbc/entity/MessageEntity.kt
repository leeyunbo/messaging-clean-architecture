package com.messaging.infrastructure.r2dbc.entity

import com.messaging.core.message.domain.*
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * Message R2DBC Entity
 */
@Table("messages")
data class MessageEntity(
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

    @Column("content")
    val content: String,

    @Column("detail")
    val detail: String = "{}",

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
    fun toDomain(detailMap: Map<String, Any?> = emptyMap()): Message = Message(
        id = id,
        messageId = messageId,
        partnerId = partnerId,
        clientMsgId = clientMsgId,
        type = MessageType.valueOf(type),
        carrier = carrier?.let { Carrier.valueOf(it) },
        recipient = recipient,
        content = content,
        detail = detailMap,
        status = MessageStatus.valueOf(status),
        retryCount = retryCount,
        resultCode = resultCode,
        resultMessage = resultMessage,
        sentAt = sentAt,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(message: Message, detailJson: String): MessageEntity = MessageEntity(
            id = message.id,
            messageId = message.messageId,
            partnerId = message.partnerId,
            clientMsgId = message.clientMsgId,
            type = message.type.name,
            carrier = message.carrier?.name,
            recipient = message.recipient,
            content = message.content,
            detail = detailJson,
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
