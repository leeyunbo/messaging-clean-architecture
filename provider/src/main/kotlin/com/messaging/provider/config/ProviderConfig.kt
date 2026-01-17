package com.messaging.provider.config

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

@Configuration
class ProviderConfig {

    @Value("\${provider.base-url:http://localhost:8090}")
    private lateinit var baseUrl: String

    @Bean
    fun providerWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .build()
    }

    @Bean
    fun circuitBreakerRegistry(): CircuitBreakerRegistry {
        val config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50f)                      // 실패율 50% 이상이면 OPEN
            .slowCallRateThreshold(80f)                     // 느린 호출 80% 이상이면 OPEN
            .slowCallDurationThreshold(Duration.ofSeconds(3)) // 3초 이상이면 느린 호출
            .waitDurationInOpenState(Duration.ofSeconds(30))  // OPEN 상태 30초 유지
            .slidingWindowSize(10)                          // 최근 10개 호출 기준
            .minimumNumberOfCalls(5)                        // 최소 5번 호출 후 판단
            .permittedNumberOfCallsInHalfOpenState(3)       // HALF_OPEN에서 3번 테스트
            .build()

        return CircuitBreakerRegistry.of(config)
    }

    @Bean
    fun providerCircuitBreaker(registry: CircuitBreakerRegistry): CircuitBreaker {
        return registry.circuitBreaker("provider")
    }
}
