package com.messaging.platform.skt.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * SKT SMS 발송 요청
 */
data class SktSmsRequest(
    @JsonProperty("msg_id")
    val msgId: String,

    @JsonProperty("phone")
    val phone: String,

    @JsonProperty("callback")
    val callback: String,

    @JsonProperty("msg")
    val msg: String,

    @JsonProperty("msg_type")
    val msgType: String = "SMS"
)

/**
 * SKT LMS/MMS 발송 요청
 */
data class SktLmsRequest(
    @JsonProperty("msg_id")
    val msgId: String,

    @JsonProperty("phone")
    val phone: String,

    @JsonProperty("callback")
    val callback: String,

    @JsonProperty("subject")
    val subject: String? = null,

    @JsonProperty("msg")
    val msg: String,

    @JsonProperty("msg_type")
    val msgType: String = "LMS",

    @JsonProperty("file_path")
    val filePath: String? = null
)
