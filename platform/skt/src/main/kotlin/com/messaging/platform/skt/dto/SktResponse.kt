package com.messaging.platform.skt.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * SKT API 응답
 */
data class SktResponse(
    @JsonProperty("result_code")
    val resultCode: String,

    @JsonProperty("result_msg")
    val resultMsg: String,

    @JsonProperty("msg_id")
    val msgId: String? = null
) {
    fun isSuccess(): Boolean = resultCode == "0000"
}
