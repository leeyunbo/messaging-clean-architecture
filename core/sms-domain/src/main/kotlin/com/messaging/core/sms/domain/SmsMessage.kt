package com.messaging.core.sms.domain

import java.time.LocalDateTime

/**
 * SMS 메시지 도메인 객체
 */
data class SmsMessage(
    val id: Long? = null,
    val messageId: String,
    val partnerId: String,
    val clientMsgId: String? = null,
    val type: SmsType,
    val carrier: Carrier? = null,
    val recipient: String,
    val callback: String,
    val content: String,
    val subject: String? = null,           // LMS/MMS용
    val imageUrl: String? = null,          // MMS용
    val status: SmsStatus = SmsStatus.PENDING,
    val retryCount: Int = 0,
    val resultCode: String? = null,
    val resultMessage: String? = null,
    val sentAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * SMS 타입
 */
enum class SmsType {
    SMS,    // 단문 (90바이트 이하)
    LMS,    // 장문 (2000바이트 이하)
    MMS     // 멀티미디어 (이미지 포함)
}

/**
 * 통신사
 */
enum class Carrier {
    SKT,
    KT,
    LGT
}

/**
 * SMS 상태
 */
enum class SmsStatus {
    PENDING,    // 대기
    SENDING,    // 발송 중
    SUCCESS,    // 성공
    FAILED,     // 실패
    EXPIRED     // 만료
}

/**
 * SMS 기본값
 */
object SmsDefaults {
    /** 기본 발신번호 (대표번호) */
    const val DEFAULT_CALLBACK = "1588-0000"
}
