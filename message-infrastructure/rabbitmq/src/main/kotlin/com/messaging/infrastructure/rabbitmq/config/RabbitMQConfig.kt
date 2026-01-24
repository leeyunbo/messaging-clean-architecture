package com.messaging.infrastructure.rabbitmq.config

import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.amqp.support.converter.SmartMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.ParameterizedTypeReference
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * RabbitMQ 설정 (Spring AMQP + Jackson 3)
 */
@Configuration
class RabbitMQConfig {

    @Bean
    fun rabbitJsonMapper(): JsonMapper {
        return JsonMapper.builder()
            .addModule(KotlinModule.Builder().build())
            .build()
    }

    @Bean
    fun messageConverter(rabbitJsonMapper: JsonMapper): MessageConverter {
        return Jackson3MessageConverter(rabbitJsonMapper)
    }

    @Bean
    fun rabbitTemplate(
        connectionFactory: ConnectionFactory,
        messageConverter: MessageConverter
    ): RabbitTemplate {
        return RabbitTemplate(connectionFactory).apply {
            this.messageConverter = messageConverter
        }
    }
}

/**
 * Jackson 3 기반 SmartMessageConverter
 * @RabbitListener 메서드의 파라미터 타입을 자동으로 역직렬화
 */
class Jackson3MessageConverter(
    private val jsonMapper: JsonMapper
) : SmartMessageConverter {

    override fun toMessage(obj: Any, messageProperties: MessageProperties): Message {
        val bytes = jsonMapper.writeValueAsBytes(obj)
        messageProperties.contentType = MessageProperties.CONTENT_TYPE_JSON
        messageProperties.contentEncoding = "UTF-8"
        return Message(bytes, messageProperties)
    }

    override fun fromMessage(message: Message): Any {
        // 타입 정보 없이 호출되면 Map으로 역직렬화
        return jsonMapper.readValue(message.body, Map::class.java)
    }

    override fun fromMessage(message: Message, conversionHint: Any?): Any {
        val targetType = when (conversionHint) {
            is ParameterizedTypeReference<*> -> conversionHint.type
            is Class<*> -> conversionHint
            is Type -> conversionHint
            else -> return fromMessage(message)
        }

        val javaType = when (targetType) {
            is Class<*> -> jsonMapper.constructType(targetType)
            is ParameterizedType -> jsonMapper.constructType(targetType)
            else -> jsonMapper.constructType(targetType)
        }

        return jsonMapper.readValue(message.body, javaType)
    }
}
