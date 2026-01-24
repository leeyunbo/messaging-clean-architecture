package com.messaging.core.sms.domain

/**
 * SMS 발송 요청
 * SMS/LMS/MMS 발송에 필요한 모든 정보를 타입 안전하게 정의
 */
data class SmsSendRequest(
    val messageId: String,
    val type: SmsType,
    val carrier: Carrier? = null,
    val recipient: String,
    val callback: String,
    val content: String,
    val subject: String? = null,      // LMS/MMS 제목
    val imageUrl: String? = null      // MMS 이미지 URL
) {
    init {
        require(recipient.matches(Regex("^01[0-9]{8,9}$"))) {
            "Invalid phone number format: $recipient"
        }
        require(content.isNotBlank()) {
            "Content must not be blank"
        }
        if (type == SmsType.MMS) {
            require(!imageUrl.isNullOrBlank()) {
                "MMS requires imageUrl"
            }
        }
    }

    companion object {
        fun sms(
            messageId: String,
            recipient: String,
            callback: String,
            content: String,
            carrier: Carrier? = null
        ) = SmsSendRequest(
            messageId = messageId,
            type = SmsType.SMS,
            carrier = carrier,
            recipient = recipient,
            callback = callback,
            content = content
        )

        fun lms(
            messageId: String,
            recipient: String,
            callback: String,
            content: String,
            subject: String? = null,
            carrier: Carrier? = null
        ) = SmsSendRequest(
            messageId = messageId,
            type = SmsType.LMS,
            carrier = carrier,
            recipient = recipient,
            callback = callback,
            content = content,
            subject = subject
        )

        fun mms(
            messageId: String,
            recipient: String,
            callback: String,
            content: String,
            imageUrl: String,
            subject: String? = null,
            carrier: Carrier? = null
        ) = SmsSendRequest(
            messageId = messageId,
            type = SmsType.MMS,
            carrier = carrier,
            recipient = recipient,
            callback = callback,
            content = content,
            subject = subject,
            imageUrl = imageUrl
        )
    }
}
