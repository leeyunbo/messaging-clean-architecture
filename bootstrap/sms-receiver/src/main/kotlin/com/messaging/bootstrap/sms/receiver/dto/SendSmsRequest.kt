package com.messaging.bootstrap.sms.receiver.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

/**
 * SMS 발송 요청 DTO
 */
data class SendSmsRequest(
    @field:NotBlank(message = "clientMsgId is required")
    val clientMsgId: String? = null,

    @field:NotBlank(message = "recipient is required")
    @field:Pattern(regexp = "^01[0-9]{8,9}$", message = "Invalid phone number format")
    val recipient: String,

    @field:NotBlank(message = "content is required")
    val content: String,

    val callback: String? = null,

    val carrier: String? = null
)

/**
 * SMS 발송 응답 DTO
 */
data class SendSmsResponse(
    val messageId: String,
    val status: String,
    val message: String
)
