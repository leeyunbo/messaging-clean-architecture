package com.messaging.platform.kakao.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 카카오 알림톡 발송 요청 DTO
 */
data class KakaoAlimtalkRequestDto(
    @JsonProperty("senderKey")
    val senderKey: String,

    @JsonProperty("templateCode")
    val templateCode: String,

    @JsonProperty("requestId")
    val requestId: String,

    @JsonProperty("recipientList")
    val recipientList: List<KakaoRecipientDto>
)

data class KakaoRecipientDto(
    @JsonProperty("recipientNo")
    val recipientNo: String,

    @JsonProperty("templateParameter")
    val templateParameter: Map<String, String>? = null
)

/**
 * 카카오 알림톡 API 응답
 */
data class KakaoResponse(
    @JsonProperty("code")
    val code: Int,

    @JsonProperty("message")
    val message: String,

    @JsonProperty("data")
    val data: KakaoResponseData? = null
) {
    fun isSuccess(): Boolean = code == 0
}

data class KakaoResponseData(
    @JsonProperty("requestId")
    val requestId: String? = null
)
