package com.messaging.bootstrap.rcs

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "webhook.rcs")
data class RcsWebhookProperties(
    val secretKey: String = ""
)
