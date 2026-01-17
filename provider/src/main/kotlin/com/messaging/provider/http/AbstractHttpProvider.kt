package com.messaging.provider.http

import com.messaging.provider.MessageProvider
import com.messaging.provider.SendRequest
import com.messaging.provider.SendResult
import com.messaging.provider.VendorRequest
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.Exceptions
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.TimeoutException

private val log = KotlinLogging.logger {}

/**
 * HTTP 기반 Provider 추상 클래스
 * @param R 벤더별 응답 타입
 */
abstract class AbstractHttpProvider<R : Any>(
    private val webClient: WebClient,
    private val circuitBreaker: CircuitBreaker,
    private val responseClass: Class<R>
) : MessageProvider {

    protected abstract val logPrefix: String

    protected abstract val endpoint: String

    protected open val timeout: Duration = Duration.ofSeconds(5)

    protected open val maxRetries: Int = 3

    override fun send(request: SendRequest): Mono<SendResult> {
        val vendorRequest = buildVendorRequest(request)
        log.debug { "[$logPrefix] Sending request to $endpoint: $vendorRequest" }

        return webClient.post()
            .uri(endpoint)
            .headers { headers ->
                vendorRequest.headers.forEach { (key, value) ->
                    headers.add(key, value?.toString())
                }
            }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(vendorRequest.body)
            .retrieve()
            .bodyToMono(responseClass)
            .timeout(timeout)
            .map { response -> mapToSendResult(response) }
            .transform(CircuitBreakerOperator.of(circuitBreaker))
            .retryWhen(retrySpec())
            .onErrorResume { error -> handleError(error) }
            .doOnSuccess { result ->
                result?.let {
                    log.info { "[$logPrefix] Result: ${it.resultCode} - ${it.resultMessage}" }
                }
            }
    }

    protected abstract fun buildVendorRequest(request: SendRequest): VendorRequest

    protected abstract fun mapToSendResult(response: R): SendResult

    private fun retrySpec() = reactor.util.retry.Retry
        .backoff(maxRetries.toLong(), Duration.ofMillis(500))
        .filter { error -> error is WebClientRequestException }
        .doBeforeRetry { signal ->
            log.warn { "[$logPrefix] Retry attempt ${signal.totalRetries() + 1}/$maxRetries: ${signal.failure().message}" }
        }

    private fun handleError(error: Throwable): Mono<SendResult> {
        log.error(error) { "[$logPrefix] Error occurred: ${error.message}" }
        return Mono.just(error.toSendResult())
    }

    private fun Throwable.toSendResult(): SendResult {
        // RetryExhaustedException의 경우 원인 예외로 처리
        val actual = if (Exceptions.isRetryExhausted(this)) cause ?: this else this
        return when (actual) {
            is TimeoutException -> SendResult.fail("TIMEOUT", "요청 타임아웃", retryable = true)
            is WebClientRequestException -> SendResult.fail("CONNECTION_ERROR", "연결 실패: ${actual.message}", retryable = true)
            is WebClientResponseException -> SendResult.fail("HTTP_${actual.statusCode.value()}", "HTTP 오류: ${actual.statusText}")
            is CallNotPermittedException -> SendResult.fail("CIRCUIT_OPEN", "서킷브레이커 OPEN 상태", retryable = true)
            else -> SendResult.fail("UNKNOWN_ERROR", "알 수 없는 오류: ${actual.message}")
        }
    }
}
