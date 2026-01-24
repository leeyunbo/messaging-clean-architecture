package com.messaging.infrastructure.r2dbc.entity

import com.messaging.core.partner.domain.Partner
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

/**
 * Partner R2DBC Entity
 */
@Table("partners")
data class PartnerEntity(
    @Id
    val id: Long? = null,

    @Column("partner_id")
    val partnerId: String,

    @Column("partner_name")
    val partnerName: String,

    @Column("api_key")
    val apiKey: String,

    @Column("api_secret")
    val apiSecret: String,

    @Column("webhook_url")
    val webhookUrl: String,

    @Column("webhook_secret")
    val webhookSecret: String,

    @Column("rate_limit_per_second")
    val rateLimitPerSecond: Int = 100,

    @Column("active")
    val active: Boolean = true,

    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column("updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDomain(): Partner = Partner(
        id = id,
        partnerId = partnerId,
        partnerName = partnerName,
        apiKey = apiKey,
        apiSecret = apiSecret,
        webhookUrl = webhookUrl,
        webhookSecret = webhookSecret,
        rateLimitPerSecond = rateLimitPerSecond,
        active = active,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(partner: Partner): PartnerEntity = PartnerEntity(
            id = partner.id,
            partnerId = partner.partnerId,
            partnerName = partner.partnerName,
            apiKey = partner.apiKey,
            apiSecret = partner.apiSecret,
            webhookUrl = partner.webhookUrl,
            webhookSecret = partner.webhookSecret,
            rateLimitPerSecond = partner.rateLimitPerSecond,
            active = partner.active,
            createdAt = partner.createdAt,
            updatedAt = partner.updatedAt
        )
    }
}
