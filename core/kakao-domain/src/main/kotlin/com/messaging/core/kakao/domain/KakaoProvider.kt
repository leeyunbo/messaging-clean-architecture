package com.messaging.core.kakao.domain

/**
 * 알림톡 Provider 인터페이스 (Port)
 */
interface AlimtalkProvider {
    suspend fun send(request: AlimtalkRequest): KakaoSendResult
}

/**
 * 브랜드메시지 Provider 인터페이스 (Port) - Kakao Direct
 */
interface BrandMessageProvider {
    suspend fun send(request: BrandMessageRequest): KakaoSendResult
}
