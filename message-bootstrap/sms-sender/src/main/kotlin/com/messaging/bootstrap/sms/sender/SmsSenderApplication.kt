package com.messaging.bootstrap.sms.sender

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.messaging.bootstrap.sms.sender",
        "com.messaging.core",
        "com.messaging.infrastructure",
        "com.messaging.platform.skt",
        "com.messaging.platform.kt",
        "com.messaging.platform.lgt",
        "com.messaging.usecase.sms",
        "com.messaging.library"
    ]
)
@ConfigurationPropertiesScan(
    basePackages = [
        "com.messaging.platform.skt.config",
        "com.messaging.platform.kt.config",
        "com.messaging.platform.lgt.config"
    ]
)
class SmsSenderApplication

fun main(args: Array<String>) {
    runApplication<SmsSenderApplication>(*args)
}
