package com.messaging.provider

import com.messaging.common.domain.Carrier
import com.messaging.common.domain.MessageType
import reactor.core.publisher.Mono

/**
 * 메시지 발송 Provider 인터페이스
 * - 벤더 API 호출 및 응답 수신만 담당
 */
interface MessageProvider {
    /**
     * 지원하는 메시지 타입 목록
     */
    fun supportedTypes(): Set<MessageType>

    /**
     * 지원하는 통신사 (null이면 통신사 구분 없음)
     */
    fun supportedCarrier(): Carrier? = null

    /**
     * 메시지 발송
     */
    fun send(request: SendRequest): Mono<SendResult>
}
