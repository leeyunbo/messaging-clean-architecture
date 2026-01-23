package com.messaging.bootstrap.kakao

import org.springframework.amqp.core.Queue
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * RabbitMQ 큐 선언
 */
@Configuration
class RabbitConfig {

    @Bean
    fun alimtalkQueue(@Value("\${rabbitmq.queue}") queueName: String): Queue {
        return Queue(queueName, true)  // durable = true
    }
}
