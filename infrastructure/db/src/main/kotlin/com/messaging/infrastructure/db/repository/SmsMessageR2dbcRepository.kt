package com.messaging.infrastructure.db.repository

import com.messaging.infrastructure.db.entity.SmsMessageEntity
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface SmsMessageR2dbcRepository : CoroutineCrudRepository<SmsMessageEntity, Long> {

    suspend fun findByMessageId(messageId: String): SmsMessageEntity?

    suspend fun findByPartnerIdAndClientMsgId(partnerId: String, clientMsgId: String): SmsMessageEntity?

    @Modifying
    @Query("UPDATE sms_messages SET status = :status, updated_at = :updatedAt WHERE message_id = :messageId")
    suspend fun updateStatus(messageId: String, status: String, updatedAt: LocalDateTime): Int

    @Modifying
    @Query("""
        UPDATE sms_messages
        SET status = :status,
            result_code = :resultCode,
            result_message = :resultMessage,
            sent_at = :sentAt,
            updated_at = :updatedAt
        WHERE message_id = :messageId
    """)
    suspend fun updateResult(
        messageId: String,
        status: String,
        resultCode: String,
        resultMessage: String,
        sentAt: LocalDateTime?,
        updatedAt: LocalDateTime
    ): Int

    @Modifying
    @Query("""
        UPDATE sms_messages
        SET retry_count = retry_count + 1,
            status = 'PENDING',
            updated_at = :updatedAt
        WHERE message_id = :messageId
    """)
    suspend fun incrementRetryCount(messageId: String, updatedAt: LocalDateTime): Int
}
