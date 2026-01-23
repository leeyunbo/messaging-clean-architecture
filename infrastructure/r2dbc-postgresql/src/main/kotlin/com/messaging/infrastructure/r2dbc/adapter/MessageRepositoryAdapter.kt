package com.messaging.infrastructure.r2dbc.adapter

import com.messaging.core.message.domain.Message
import com.messaging.core.message.domain.MessageRepository
import com.messaging.core.message.domain.MessageStatus
import com.messaging.infrastructure.r2dbc.entity.MessageEntity
import com.messaging.infrastructure.r2dbc.repository.MessageR2dbcRepository
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper
import java.time.LocalDateTime

@Component
class MessageRepositoryAdapter(
    private val r2dbcRepository: MessageR2dbcRepository,
    private val jsonMapper: JsonMapper
) : MessageRepository {

    override suspend fun save(message: Message): Message {
        val detailJson = jsonMapper.writeValueAsString(message.detail)
        val entity = MessageEntity.fromDomain(message, detailJson)
        val saved = r2dbcRepository.save(entity)
        return saved.toDomain(message.detail)
    }

    override suspend fun findByMessageId(messageId: String): Message? {
        return r2dbcRepository.findByMessageId(messageId)?.let { entity ->
            val detailMap = parseDetail(entity.detail)
            entity.toDomain(detailMap)
        }
    }

    override suspend fun findByPartnerIdAndClientMsgId(partnerId: String, clientMsgId: String): Message? {
        return r2dbcRepository.findByPartnerIdAndClientMsgId(partnerId, clientMsgId)?.let { entity ->
            val detailMap = parseDetail(entity.detail)
            entity.toDomain(detailMap)
        }
    }

    override suspend fun updateStatus(messageId: String, status: MessageStatus) {
        r2dbcRepository.updateStatus(messageId, status.name, LocalDateTime.now())
    }

    override suspend fun updateResult(
        messageId: String,
        status: MessageStatus,
        resultCode: String,
        resultMessage: String
    ) {
        val sentAt = if (status == MessageStatus.SUCCESS) LocalDateTime.now() else null
        r2dbcRepository.updateResult(
            messageId = messageId,
            status = status.name,
            resultCode = resultCode,
            resultMessage = resultMessage,
            sentAt = sentAt,
            updatedAt = LocalDateTime.now()
        )
    }

    override suspend fun incrementRetryCount(messageId: String) {
        r2dbcRepository.incrementRetryCount(messageId, LocalDateTime.now())
    }

    private fun parseDetail(detailJson: String): Map<String, Any?> {
        return try {
            @Suppress("UNCHECKED_CAST")
            jsonMapper.readValue(detailJson, Map::class.java) as Map<String, Any?>
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
