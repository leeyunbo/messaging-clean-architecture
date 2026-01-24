package com.messaging.infrastructure.db.polling

import com.messaging.core.kakao.domain.AlimtalkPollingItem
import com.messaging.core.kakao.domain.AlimtalkPollingStore
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * R2DBC 기반 알림톡 폴링 저장소 구현
 */
@Component
class R2dbcAlimtalkPollingStore(
    private val repository: AlimtalkPollingRepository
) : AlimtalkPollingStore {
    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun save(item: AlimtalkPollingItem) {
        val entity = AlimtalkPollingEntity(
            messageId = item.messageId,
            serialNumber = item.serialNumber
        )
        repository.save(entity)
        log.debug("Polling item saved: messageId={}, serialNumber={}", item.messageId, item.serialNumber)
    }
}
