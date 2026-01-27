package com.messaging.bootstrap.naver

import org.springframework.amqp.core.Queue
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {

    @Bean
    fun naverQueue(@Value("\${rabbitmq.queue}") queueName: String): Queue {
        return Queue(queueName, true)
    }
}
