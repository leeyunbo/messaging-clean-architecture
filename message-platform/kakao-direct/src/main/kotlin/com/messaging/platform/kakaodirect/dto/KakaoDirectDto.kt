package com.messaging.platform.kakaodirect.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 카카오 다이렉트 브랜드메시지 발송 요청 DTO
 */
data class BrandMessageRequestDto(
    @JsonProperty("channelId")
    val channelId: String,

    @JsonProperty("requestId")
    val requestId: String,

    @JsonProperty("recipient")
    val recipient: BrandMessageRecipientDto,

    @JsonProperty("content")
    val content: BrandMessageContentDto
)

data class BrandMessageRecipientDto(
    @JsonProperty("phoneNumber")
    val phoneNumber: String
)

data class BrandMessageContentDto(
    @JsonProperty("text")
    val text: String,

    @JsonProperty("imageUrl")
    val imageUrl: String? = null,

    @JsonProperty("buttons")
    val buttons: List<BrandMessageButtonDto>? = null
)

data class BrandMessageButtonDto(
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
 * 카카오 다이렉트 API 응답
 */
data class KakaoDirectResponse(
    @JsonProperty("code")
    val code: String,

    @JsonProperty("message")
    val message: String,

    @JsonProperty("data")
    val data: KakaoDirectResponseData? = null
) {
    fun isSuccess(): Boolean = code == "0000"
}

data class KakaoDirectResponseData(
    @JsonProperty("requestId")
    val requestId: String? = null,

    @JsonProperty("messageId")
    val messageId: String? = null
)
