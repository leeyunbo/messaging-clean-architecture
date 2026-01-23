package com.messaging.bootstrap.sms.sender.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.kotlinModule

/**
 * Jackson 3.0 설정
 * - java.time 지원은 jackson-databind에 내장됨 (JavaTimeModule 별도 등록 불필요)
 * - 날짜는 기본적으로 ISO-8601 문자열로 직렬화됨 (Jackson 3.0 기본값)
 */
@Configuration
class JacksonConfig {

    @Bean
    fun jsonMapper(): JsonMapper {
        return JsonMapper.builder()
            .addModule(kotlinModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build()
    }
}
