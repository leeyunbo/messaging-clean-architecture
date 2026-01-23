package com.messaging.core.sms.domain

/**
 * SMS Provider 인터페이스 (Port)
 * platform 모듈에서 구현
 */
interface SmsProvider {
    /**
     * 지원하는 SMS 타입
     */
    fun supportedTypes(): Set<SmsType>

    /**
     * 지원하는 통신사 (null이면 모든 통신사)
     */
    fun supportedCarrier(): Carrier?

    /**
     * SMS 발송
     */
    suspend fun send(request: SmsSendRequest): SmsSendResult
}
