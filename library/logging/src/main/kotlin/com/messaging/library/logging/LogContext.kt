package com.messaging.library.logging

import org.slf4j.MDC

/**
 * 로깅 컨텍스트 관리
 * MDC를 활용한 구조화된 로깅 지원
 */
object LogContext {
    const val MESSAGE_ID = "messageId"
    const val PARTNER_ID = "partnerId"
    const val CARRIER = "carrier"
    const val MESSAGE_TYPE = "messageType"
    const val TRACE_ID = "traceId"

    /**
     * 메시지 컨텍스트 설정
     */
    fun withMessage(
        messageId: String,
        partnerId: String? = null,
        carrier: String? = null,
        messageType: String? = null
    ): AutoCloseable {
        MDC.put(MESSAGE_ID, messageId)
        partnerId?.let { MDC.put(PARTNER_ID, it) }
        carrier?.let { MDC.put(CARRIER, it) }
        messageType?.let { MDC.put(MESSAGE_TYPE, it) }

        return AutoCloseable { clear() }
    }

    /**
     * 트레이스 ID 설정
     */
    fun withTraceId(traceId: String): AutoCloseable {
        MDC.put(TRACE_ID, traceId)
        return AutoCloseable { MDC.remove(TRACE_ID) }
    }

    /**
     * 현재 메시지 ID 조회
     */
    fun getMessageId(): String? = MDC.get(MESSAGE_ID)

    /**
     * 현재 트레이스 ID 조회
     */
    fun getTraceId(): String? = MDC.get(TRACE_ID)

    /**
     * 모든 컨텍스트 클리어
     */
    fun clear() {
        MDC.remove(MESSAGE_ID)
        MDC.remove(PARTNER_ID)
        MDC.remove(CARRIER)
        MDC.remove(MESSAGE_TYPE)
        MDC.remove(TRACE_ID)
    }
}
