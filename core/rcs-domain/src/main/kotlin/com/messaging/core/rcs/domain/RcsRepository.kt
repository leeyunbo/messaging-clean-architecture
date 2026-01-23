package com.messaging.core.rcs.domain

/**
 * RCS 메시지 저장소 인터페이스 (Port)
 */
interface RcsRepository {
    suspend fun save(message: RcsMessage): RcsMessage
    suspend fun findByMessageId(messageId: String): RcsMessage?
    suspend fun findByPartnerIdAndClientMsgId(partnerId: String, clientMsgId: String): RcsMessage?
    suspend fun updateStatus(messageId: String, status: RcsStatus)
    suspend fun updateResult(messageId: String, status: RcsStatus, resultCode: String, resultMessage: String)
    suspend fun incrementRetryCount(messageId: String)
}
