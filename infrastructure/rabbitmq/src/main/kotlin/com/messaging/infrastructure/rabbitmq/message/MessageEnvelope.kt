package com.messaging.infrastructure.rabbitmq.message

import java.time.LocalDateTime

/**
 * 큐 메시지 봉투
 * 큐를 통해 전달되는 메시지의 표준 포맷
 * Infrastructure 레이어이므로 도메인 타입에 의존하지 않고 String 사용
 */
data class MessageEnvelope(
    val messageId: String,
    val partnerId: String,
    val clientMsgId: String? = null,
    val type: String,                        // "SMS", "ALIMTALK" 등
    val carrier: String? = null,             // "SKT", "KT", "LGT"
    val recipient: String,
    val content: String,
    val detail: Map<String, Any?> = emptyMap(),
    val retryCount: Int = 0,
    val scheduledAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
