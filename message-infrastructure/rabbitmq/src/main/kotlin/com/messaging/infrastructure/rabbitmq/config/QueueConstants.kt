package com.messaging.infrastructure.rabbitmq.config

/**
 * 큐 이름 상수 정의
 */
object QueueConstants {
    // 수신 큐 (Receiver → Sender)
    const val SMS_SEND_QUEUE = "messaging.sms.send"
    const val LMS_MMS_SEND_QUEUE = "messaging.lms-mms.send"
    const val RCS_SEND_QUEUE = "messaging.rcs.send"
    const val KAKAO_SEND_QUEUE = "messaging.kakao.send"
    const val NAVER_SEND_QUEUE = "messaging.naver.send"

    // 결과 큐 (Sender → Reporter)
    const val RESULT_QUEUE = "messaging.result"

    // 재시도 큐 (지연 발송용)
    const val SMS_RETRY_QUEUE = "messaging.sms.retry"
    const val LMS_MMS_RETRY_QUEUE = "messaging.lms-mms.retry"
    const val RCS_RETRY_QUEUE = "messaging.rcs.retry"
    const val KAKAO_RETRY_QUEUE = "messaging.kakao.retry"
    const val NAVER_RETRY_QUEUE = "messaging.naver.retry"

    // Exchange
    const val MESSAGE_EXCHANGE = "messaging.exchange"
    const val RESULT_EXCHANGE = "messaging.result.exchange"
    const val RETRY_EXCHANGE = "messaging.retry.exchange"

    // Routing Keys
    const val SMS_ROUTING_KEY = "sms"
    const val LMS_MMS_ROUTING_KEY = "lms-mms"
    const val RCS_ROUTING_KEY = "rcs"
    const val KAKAO_ROUTING_KEY = "kakao"
    const val NAVER_ROUTING_KEY = "naver"
    const val RESULT_ROUTING_KEY = "result"
}

/**
 * Consumer 기본 설정값
 */
object ConsumerDefaults {
    /** 기본 prefetch count (동시 처리 메시지 수) */
    const val DEFAULT_PREFETCH_COUNT = 10
}
