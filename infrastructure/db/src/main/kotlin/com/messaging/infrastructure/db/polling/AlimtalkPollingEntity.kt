package com.messaging.infrastructure.db.polling

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

/**
 * 알림톡 폴링 대상 엔티티
 */
@Table("alimtalk_polling")
data class AlimtalkPollingEntity(
    @Id val id: Long? = null,
    val messageId: String,
    val serialNumber: String,
    val createdAt: Instant = Instant.now()
)
