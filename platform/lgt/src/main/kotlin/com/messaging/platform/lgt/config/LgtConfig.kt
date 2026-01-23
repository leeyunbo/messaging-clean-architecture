package com.messaging.platform.lgt.config

import com.messaging.infrastructure.webclient.config.HttpDefaults
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * LG U+ 플랫폼 설정
 */
@ConfigurationProperties(prefix = "platform.lgt")
data class LgtConfig(
    val baseUrl: String = "https://api.lguplus.com",
    val apiKey: String = "",
    val timeout: Long = HttpDefaults.DEFAULT_READ_TIMEOUT_MS.toLong(),
    val enabled: Boolean = true
)
