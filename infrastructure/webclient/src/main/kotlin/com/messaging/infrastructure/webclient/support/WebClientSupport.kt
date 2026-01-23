package com.messaging.infrastructure.webclient.support

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.time.Duration

/**
 * WebClient 공통 기능 지원
 */
object WebClientSupport {
    @PublishedApi
    internal val log = LoggerFactory.getLogger(WebClientSupport::class.java)

    /**
     * POST 요청 전송 (Circuit Breaker 적용)
     */
    inline fun <reified T : Any> postWithCircuitBreaker(
        webClient: WebClient,
        circuitBreaker: CircuitBreaker,
        url: String,
        body: Any,
        timeout: Duration = Duration.ofSeconds(10)
    ): Mono<T> {
        return webClient.post()
            .uri(url)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(T::class.java)
            .timeout(timeout)
            .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
            .doOnError { e: Throwable ->
                when (e) {
                    is WebClientResponseException -> {
                        log.error("HTTP error: url={}, status={}, body={}",
                            url, e.statusCode, e.responseBodyAsString)
                    }
                    else -> {
                        log.error("Request failed: url={}, error={}", url, e.message)
                    }
                }
            }
    }

    /**
     * GET 요청 전송 (Circuit Breaker 적용)
     */
    inline fun <reified T : Any> getWithCircuitBreaker(
        webClient: WebClient,
        circuitBreaker: CircuitBreaker,
        url: String,
        timeout: Duration = Duration.ofSeconds(10)
    ): Mono<T> {
        return webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(T::class.java)
            .timeout(timeout)
            .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
            .doOnError { e: Throwable ->
                log.error("GET request failed: url={}, error={}", url, e.message)
            }
    }
}
