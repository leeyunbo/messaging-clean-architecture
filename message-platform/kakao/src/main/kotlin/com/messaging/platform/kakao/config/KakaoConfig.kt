package com.messaging.platform.kakao.config

import com.messaging.infrastructure.webclient.config.HttpDefaults
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 카카오 플랫폼 설정
 */
@ConfigurationProperties(prefix = "platform.kakao")
data class KakaoConfig(
    val baseUrl: String = "https://api.kakao.com",
    val apiKey: String = "",
    val senderKey: String = "",
    val timeout: Long = HttpDefaults.DEFAULT_READ_TIMEOUT_MS.toLong(),
    val enabled: Boolean = true
)
