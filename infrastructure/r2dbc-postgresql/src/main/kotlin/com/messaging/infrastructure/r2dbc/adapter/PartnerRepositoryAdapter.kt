package com.messaging.infrastructure.r2dbc.adapter

import com.messaging.core.partner.domain.Partner
import com.messaging.core.partner.domain.PartnerRepository
import com.messaging.infrastructure.r2dbc.entity.PartnerEntity
import com.messaging.infrastructure.r2dbc.repository.PartnerR2dbcRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component

@Component
class PartnerRepositoryAdapter(
    private val r2dbcRepository: PartnerR2dbcRepository
) : PartnerRepository {

    override suspend fun findByPartnerId(partnerId: String): Partner? {
        return r2dbcRepository.findByPartnerId(partnerId)?.toDomain()
    }

    override suspend fun findByApiKey(apiKey: String): Partner? {
        return r2dbcRepository.findByApiKey(apiKey)?.toDomain()
    }

    override suspend fun save(partner: Partner): Partner {
        val entity = PartnerEntity.fromDomain(partner)
        return r2dbcRepository.save(entity).toDomain()
    }

    override suspend fun findAllActive(): List<Partner> {
        return r2dbcRepository.findByActiveTrue()
            .map { it.toDomain() }
            .toList()
    }
}
