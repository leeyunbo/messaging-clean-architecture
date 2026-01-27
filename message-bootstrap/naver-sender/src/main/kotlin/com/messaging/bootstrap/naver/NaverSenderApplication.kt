package com.messaging.bootstrap.naver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.messaging"])
class NaverSenderApplication

fun main(args: Array<String>) {
    runApplication<NaverSenderApplication>(*args)
}
