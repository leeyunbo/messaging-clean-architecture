package com.messaging.platform.kakaodirect.config

import com.messaging.infrastructure.webclient.config.HttpDefaults
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 카카오 다이렉트 (브랜드메시지) 설정
 */
@ConfigurationProperties(prefix = "platform.kakao-direct")
data class KakaoDirectConfig(
    val baseUrl: String = "https://api.kakaodirect.com",
    val apiKey: String = "",
    val secretKey: String = "",
    val channelId: String = "",
    val timeout: Long = HttpDefaults.DEFAULT_READ_TIMEOUT_MS.toLong(),
    val enabled: Boolean = true
)
