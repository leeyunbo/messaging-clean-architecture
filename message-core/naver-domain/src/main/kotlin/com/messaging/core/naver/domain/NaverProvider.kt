package com.messaging.core.naver.domain

/**
 * 네이버 SMS Provider 인터페이스 (Port)
 */
interface NaverSmsProvider {
    suspend fun send(request: NaverSmsRequest): NaverSendResult
}

/**
 * 네이버 알림톡 Provider 인터페이스 (Port)
 */
interface NaverAlimtalkProvider {
    suspend fun send(request: NaverAlimtalkRequest): NaverSendResult
}
