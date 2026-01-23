package com.messaging.platform.kt.config

import com.messaging.infrastructure.webclient.config.HttpDefaults
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * KT 플랫폼 설정
 */
@ConfigurationProperties(prefix = "platform.kt")
data class KtConfig(
    val baseUrl: String = "https://api.kt.com",
    val apiKey: String = "",
    val timeout: Long = HttpDefaults.DEFAULT_READ_TIMEOUT_MS.toLong(),
    val enabled: Boolean = true
)
