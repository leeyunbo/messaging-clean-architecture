package com.messaging.core.naver.domain

interface NaverProvider {
    suspend fun send(request: NaverSendRequest): NaverSendResult
}
