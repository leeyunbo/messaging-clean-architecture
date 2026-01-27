package com.messaging.core.naver.domain

data class NaverSendRequest(
    val messageId: String,
    val recipient: String,
    val content: String
)
