package com.messaging.platform.kt.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * KT SMS 발송 요청
 */
data class KtSmsRequest(
    @JsonProperty("msgId")
    val msgId: String,

    @JsonProperty("receiver")
    val receiver: String,

    @JsonProperty("sender")
    val sender: String,

    @JsonProperty("message")
    val message: String
)

/**
 * KT API 응답
 */
data class KtResponse(
    @JsonProperty("code")
    val code: String,

    @JsonProperty("message")
    val message: String,

    @JsonProperty("data")
    val data: KtResponseData? = null
) {
    fun isSuccess(): Boolean = code == "200"
}

data class KtResponseData(
    @JsonProperty("msgId")
    val msgId: String? = null
)
