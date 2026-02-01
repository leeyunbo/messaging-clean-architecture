package com.messaging.bootstrap.rcs

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.messaging"])
class RcsWebhookApplication

fun main(args: Array<String>) {
    runApplication<RcsWebhookApplication>(*args)
}
