package com.messaging.platform.skt.config

import com.messaging.infrastructure.webclient.config.HttpDefaults
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * SKT 플랫폼 설정
 */
@ConfigurationProperties(prefix = "platform.skt")
data class SktConfig(
    val baseUrl: String = "https://api.skt.com",
    val apiKey: String = "",
    val timeout: Long = HttpDefaults.DEFAULT_READ_TIMEOUT_MS.toLong(),
    val enabled: Boolean = true
)
