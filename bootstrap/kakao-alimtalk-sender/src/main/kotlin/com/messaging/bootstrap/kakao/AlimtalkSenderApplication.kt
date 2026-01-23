package com.messaging.bootstrap.kakao

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication(scanBasePackages = ["com.messaging"])
@EnableR2dbcRepositories(basePackages = ["com.messaging"])
@ConfigurationPropertiesScan(basePackages = ["com.messaging"])
class AlimtalkSenderApplication

fun main(args: Array<String>) {
    runApplication<AlimtalkSenderApplication>(*args)
}
