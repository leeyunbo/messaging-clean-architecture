package com.messaging.core.rcs.domain

interface RcsProvider {
    suspend fun send(request: RcsSendRequest): RcsSendResult
}
