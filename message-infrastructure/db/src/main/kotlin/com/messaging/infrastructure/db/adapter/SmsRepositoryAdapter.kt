package com.messaging.infrastructure.db.adapter

import com.messaging.core.sms.domain.*
import com.messaging.infrastructure.db.entity.SmsMessageEntity
import com.messaging.infrastructure.db.repository.SmsMessageR2dbcRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * SMS Repository Adapter
 * core:sms-domain의 SmsRepository 구현
 */
@Component
class SmsRepositoryAdapter(
    private val r2dbcRepository: SmsMessageR2dbcRepository
) : SmsRepository {

    override suspend fun save(message: SmsMessage): SmsMessage {
        val entity = SmsMessageEntity.fromDomain(message)
        val saved = r2dbcRepository.save(entity)
        return saved.toDomain()
    }

    override suspend fun findByMessageId(messageId: String): SmsMessage? {
        return r2dbcRepository.findByMessageId(messageId)?.toDomain()
    }

    override suspend fun findByPartnerIdAndClientMsgId(partnerId: String, clientMsgId: String): SmsMessage? {
        return r2dbcRepository.findByPartnerIdAndClientMsgId(partnerId, clientMsgId)?.toDomain()
    }

    override suspend fun updateStatus(messageId: String, status: SmsStatus) {
        r2dbcRepository.updateStatus(messageId, status.name, LocalDateTime.now())
    }

    override suspend fun updateResult(
        messageId: String,
        status: SmsStatus,
        resultCode: String,
        resultMessage: String
    ) {
        val sentAt = if (status == SmsStatus.SUCCESS) LocalDateTime.now() else null
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
}
