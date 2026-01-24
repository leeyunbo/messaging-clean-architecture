package com.messaging.bootstrap.sms.receiver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.messaging.bootstrap.sms.receiver",
        "com.messaging.core",
        "com.messaging.infrastructure.rabbitmq",
        "com.messaging.infrastructure.r2dbc",
        "com.messaging.library"
    ]
)
@ConfigurationPropertiesScan
class SmsReceiverApplication

fun main(args: Array<String>) {
    runApplication<SmsReceiverApplication>(*args)
}
