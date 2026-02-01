package com.messaging.bootstrap.rcs.sender

import org.springframework.amqp.core.Queue
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {

    @Bean
    fun rcsQueue(@Value("\${rabbitmq.queue}") queueName: String): Queue {
        return Queue(queueName, true)
    }
}
