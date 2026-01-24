package com.messaging.platform.naver.config

import com.messaging.infrastructure.webclient.config.HttpDefaults
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 네이버 플랫폼 설정
 */
@ConfigurationProperties(prefix = "platform.naver")
data class NaverConfig(
    val baseUrl: String = "https://sens.apigw.ntruss.com",
    val accessKey: String = "",
    val secretKey: String = "",
    val serviceId: String = "",
    val timeout: Long = HttpDefaults.DEFAULT_READ_TIMEOUT_MS.toLong(),
    val enabled: Boolean = true
)
