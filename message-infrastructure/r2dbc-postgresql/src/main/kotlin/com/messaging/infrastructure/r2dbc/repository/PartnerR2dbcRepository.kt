package com.messaging.infrastructure.r2dbc.repository

import com.messaging.infrastructure.r2dbc.entity.PartnerEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PartnerR2dbcRepository : CoroutineCrudRepository<PartnerEntity, Long> {

    suspend fun findByPartnerId(partnerId: String): PartnerEntity?

    suspend fun findByApiKey(apiKey: String): PartnerEntity?

    fun findByActiveTrue(): Flow<PartnerEntity>
}
