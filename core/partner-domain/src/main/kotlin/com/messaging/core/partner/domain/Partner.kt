package com.messaging.core.partner.domain

import java.time.LocalDateTime

/**
 * 파트너 (고객사) 도메인 객체
 */
data class Partner(
    val id: Long? = null,
    val partnerId: String,
    val partnerName: String,
    val apiKey: String,
    val apiSecret: String,
    val webhookUrl: String,
    val webhookSecret: String,
    val rateLimitPerSecond: Int = 100,
    val active: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
