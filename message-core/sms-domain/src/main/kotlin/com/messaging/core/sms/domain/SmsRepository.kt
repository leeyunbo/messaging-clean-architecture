package com.messaging.core.sms.domain

/**
 * SMS 저장소 인터페이스 (Port)
 * infrastructure:db 모듈에서 구현
 */
interface SmsRepository {
    suspend fun save(message: SmsMessage): SmsMessage
    suspend fun findByMessageId(messageId: String): SmsMessage?
    suspend fun findByPartnerIdAndClientMsgId(partnerId: String, clientMsgId: String): SmsMessage?
    suspend fun updateStatus(messageId: String, status: SmsStatus)
    suspend fun updateResult(messageId: String, status: SmsStatus, resultCode: String, resultMessage: String)
    suspend fun incrementRetryCount(messageId: String)
}
