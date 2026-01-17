package com.messaging.common.domain

import com.messaging.common.converter.JsonMapConverter
import com.messaging.common.id.MessageIdGenerator
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.Collections.emptyMap

/**
 * 발송 메시지 Entity
 */
@Entity
@Table(
    name = "messages",
    indexes = [
        Index(name = "idx_message_partner_id", columnList = "partnerId"),
        Index(name = "idx_message_status", columnList = "status"),
        Index(name = "idx_message_created_at", columnList = "createdAt")
    ],
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_message_partner_client_msg",
            columnNames = ["partnerId", "clientMsgId"]
        )
    ]
)
class Message(
    @Id
    @Column(length = 20)
    val messageId: String = MessageIdGenerator.generate(),

    @Column(nullable = false, length = 50)
    val partnerId: String,

    @Column(length = 100)
    val clientMsgId: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val type: MessageType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: MessageStatus = MessageStatus.RECEIVED,

    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,

    @Convert(converter = JsonMapConverter::class)
    @Column(columnDefinition = "JSON")
    val detail: Map<String, Any?> = emptyMap(),

    @Column(length = 50)
    var providerMessageId: String? = null,

    @Column(length = 10)
    var resultCode: String? = null,

    @Column(length = 500)
    var resultMessage: String? = null,

    var sentAt: LocalDateTime? = null,
    var deliveredAt: LocalDateTime? = null,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun markAsSent(providerMsgId: String) {
        this.status = MessageStatus.SENT
        this.providerMessageId = providerMsgId
        this.sentAt = LocalDateTime.now()
    }

    fun markAsDelivered() {
        this.status = MessageStatus.DELIVERED
        this.deliveredAt = LocalDateTime.now()
    }

    fun markAsFailed(code: String, message: String) {
        this.status = MessageStatus.FAILED
        this.resultCode = code
        this.resultMessage = message
    }

    /**
     * 메시지 타입에 맞는 길이 검증
     */
    fun validateContent(): Boolean {
        return type.validate(content)
    }
}
