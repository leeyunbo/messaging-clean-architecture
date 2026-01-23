package com.messaging.core.partner.domain

/**
 * 파트너 저장소 인터페이스 (Port)
 */
interface PartnerRepository {
    suspend fun findByPartnerId(partnerId: String): Partner?
    suspend fun findByApiKey(apiKey: String): Partner?
    suspend fun save(partner: Partner): Partner
    suspend fun findAllActive(): List<Partner>
}
