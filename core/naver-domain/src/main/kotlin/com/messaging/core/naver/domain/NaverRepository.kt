package com.messaging.core.naver.domain

/**
 * 네이버 메시지 저장소 인터페이스 (Port)
 */
interface NaverRepository {
    suspend fun save(message: NaverMessage): NaverMessage
    suspend fun findByMessageId(messageId: String): NaverMessage?
    suspend fun findByPartnerIdAndClientMsgId(partnerId: String, clientMsgId: String): NaverMessage?
    suspend fun updateStatus(messageId: String, status: NaverStatus)
    suspend fun updateResult(messageId: String, status: NaverStatus, resultCode: String, resultMessage: String)
    suspend fun incrementRetryCount(messageId: String)
}
