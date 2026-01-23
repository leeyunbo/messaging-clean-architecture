package com.messaging.bootstrap.reporter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.messaging.bootstrap.reporter",
        "com.messaging.core",
        "com.messaging.infrastructure",
        "com.messaging.library"
    ]
)
class ReporterApplication

fun main(args: Array<String>) {
    runApplication<ReporterApplication>(*args)
}
