package com.messaging.infrastructure.db.polling

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

/**
 * 알림톡 폴링 대상 R2DBC 리포지토리
 */
@Repository
interface AlimtalkPollingRepository : CoroutineCrudRepository<AlimtalkPollingEntity, Long>
