package com.messaging.bootstrap.rcs.receiver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.messaging"])
class RcsReceiverApplication

fun main(args: Array<String>) {
    runApplication<RcsReceiverApplication>(*args)
}
