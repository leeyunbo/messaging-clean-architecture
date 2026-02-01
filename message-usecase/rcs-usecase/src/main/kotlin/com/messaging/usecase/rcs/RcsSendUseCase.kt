package com.messaging.usecase.rcs

import com.messaging.core.rcs.domain.RcsProvider
import com.messaging.core.rcs.domain.RcsSendRequest
import com.messaging.core.rcs.domain.RcsSendResult
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RcsSendUseCase(
    private val rcsProvider: RcsProvider
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun send(request: RcsSendRequest): RcsSendResult {
        log.info("Processing RCS send: messageId={}", request.messageId)

        val result = rcsProvider.send(request)
        log.info("RCS send completed: messageId={}, success={}, requestId={}",
            request.messageId, result.success, result.requestId)

        return result
    }
}
