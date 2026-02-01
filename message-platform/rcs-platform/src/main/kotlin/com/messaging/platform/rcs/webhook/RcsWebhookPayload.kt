package com.messaging.platform.rcs.webhook

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * RCS 웹훅 수신 페이로드
 */
data class RcsWebhookPayload(
    @param:JsonProperty("messageId")
    val messageId: String,

    @param:JsonProperty("status")
    val status: String,

    @param:JsonProperty("resultCode")
    val resultCode: String,

    @param:JsonProperty("resultMessage")
    val resultMessage: String? = null,

    @param:JsonProperty("sentAt")
    val sentAt: String? = null,

    @param:JsonProperty("deliveredAt")
    val deliveredAt: String? = null
)

/**
 * RCS 웹훅 상태
 */
object RcsWebhookStatus {
    const val DELIVERED = "DELIVERED"
    const val FAILED = "FAILED"
    const val EXPIRED = "EXPIRED"
    const val READ = "READ"
}
