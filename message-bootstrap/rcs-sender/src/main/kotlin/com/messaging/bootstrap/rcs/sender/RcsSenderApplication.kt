package com.messaging.bootstrap.rcs.sender

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.messaging"])
class RcsSenderApplication

fun main(args: Array<String>) {
    runApplication<RcsSenderApplication>(*args)
}
