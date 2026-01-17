package com.messaging.provider.config

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ProviderConfigTest {

    @Test
    fun `CircuitBreakerRegistry 생성 확인`() {
        // Given
        val config = ProviderConfig()

        // When
        val registry = config.circuitBreakerRegistry()

        // Then
        assertNotNull(registry)
    }

    @Test
    fun `CircuitBreaker 생성 확인`() {
        // Given
        val config = ProviderConfig()
        val registry = config.circuitBreakerRegistry()

        // When
        val circuitBreaker = config.providerCircuitBreaker(registry)

        // Then
        assertNotNull(circuitBreaker)
    }
}
