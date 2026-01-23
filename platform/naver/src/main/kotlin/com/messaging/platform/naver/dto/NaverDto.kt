package com.messaging.platform.naver.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 네이버 클라우드 SMS 발송 요청 DTO
 */
data class NaverSmsRequestDto(
    @JsonProperty("type")
    val type: String,

    @JsonProperty("contentType")
    val contentType: String = "COMM",

    @JsonProperty("countryCode")
    val countryCode: String = "82",

    @JsonProperty("from")
    val from: String,

    @JsonProperty("content")
    val content: String,

    @JsonProperty("messages")
    val messages: List<NaverSmsMessageDto>
)

data class NaverSmsMessageDto(
    @JsonProperty("to")
    val to: String,

    @JsonProperty("subject")
    val subject: String? = null,

    @JsonProperty("content")
    val content: String? = null
)

/**
 * 네이버 클라우드 알림톡 발송 요청 DTO
 */
data class NaverAlimtalkRequestDto(
    @JsonProperty("templateCode")
    val templateCode: String,

    @JsonProperty("plusFriendId")
    val plusFriendId: String,

    @JsonProperty("messages")
    val messages: List<NaverAlimtalkMessageDto>
)

data class NaverAlimtalkMessageDto(
    @JsonProperty("to")
    val to: String,

    @JsonProperty("content")
    val content: String,

    @JsonProperty("buttons")
    val buttons: List<NaverButtonDto>? = null
)

data class NaverButtonDto(
    @JsonProperty("type")
    val type: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("linkMobile")
    val linkMobile: String? = null,

    @JsonProperty("linkPc")
    val linkPc: String? = null
)

/**
 * 네이버 클라우드 API 응답
 */
data class NaverResponse(
    @JsonProperty("statusCode")
    val statusCode: String,

    @JsonProperty("statusName")
    val statusName: String,

    @JsonProperty("requestId")
    val requestId: String? = null
) {
    fun isSuccess(): Boolean = statusCode == "202"
}
