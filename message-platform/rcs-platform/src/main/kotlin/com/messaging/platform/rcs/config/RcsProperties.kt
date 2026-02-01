package com.messaging.platform.rcs.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "platform.rcs")
data class RcsProperties(
    val baseUrl: String = "https://rcs-api.example.com",
    val apiKey: String = "",
    val brandId: String = "",
    val timeout: Long = 10_000L,
    val enabled: Boolean = true
)
