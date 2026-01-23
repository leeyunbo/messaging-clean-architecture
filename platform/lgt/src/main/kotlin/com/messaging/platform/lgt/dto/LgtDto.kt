package com.messaging.platform.lgt.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * LG U+ 발송 요청
 */
data class LgtRequest(
    @JsonProperty("transaction_id")
    val transactionId: String,

    @JsonProperty("to")
    val to: String,

    @JsonProperty("from")
    val from: String,

    @JsonProperty("text")
    val text: String,

    @JsonProperty("type")
    val type: String = "SMS"
)

/**
 * LG U+ API 응답
 */
data class LgtResponse(
    @JsonProperty("status")
    val status: String,

    @JsonProperty("error_code")
    val errorCode: String? = null,

    @JsonProperty("error_message")
    val errorMessage: String? = null,

    @JsonProperty("transaction_id")
    val transactionId: String? = null
) {
    fun isSuccess(): Boolean = status == "success"
}
