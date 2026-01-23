package com.messaging.infrastructure.rabbitmq.message

import java.time.LocalDateTime

/**
 * 결과 메시지 봉투
 * 발송 결과를 Reporter로 전달하는 표준 포맷
 * Infrastructure 레이어이므로 도메인 타입에 의존하지 않고 String 사용
 */
data class ResultEnvelope(
    val messageId: String,
    val partnerId: String,
    val clientMsgId: String? = null,
    val status: String,                      // "SUCCESS", "FAILED", "PENDING" 등
    val resultCode: String? = null,
    val resultMessage: String? = null,
    val sentAt: LocalDateTime? = null,
    val processedAt: LocalDateTime = LocalDateTime.now()
)
