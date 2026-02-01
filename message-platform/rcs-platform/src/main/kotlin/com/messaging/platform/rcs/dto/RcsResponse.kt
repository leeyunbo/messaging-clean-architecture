package com.messaging.platform.rcs.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RcsResponse(
    @param:JsonProperty("resultCode")
    val resultCode: String,

    @param:JsonProperty("resultMessage")
    val resultMessage: String,

    @param:JsonProperty("requestId")
    val requestId: String? = null
) {
    fun isSuccess(): Boolean = resultCode == "0000"
}
