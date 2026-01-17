package com.messaging.provider.http

import com.messaging.common.domain.Carrier
import com.messaging.common.domain.MessageType
import com.messaging.provider.MockWebServerTestBase
import com.messaging.provider.SendResult
import com.messaging.provider.VendorRequest
import com.messaging.provider.mockDisconnect
import com.messaging.provider.mockHttpError
import com.messaging.provider.mockSuccess
import com.messaging.provider.mockTimeout
import com.messaging.provider.shouldBeFail
import com.messaging.provider.shouldBeRetryable
import com.messaging.provider.shouldBeSuccess
import com.messaging.provider.shouldHaveCode
import com.messaging.provider.shouldHaveMessage
import com.messaging.provider.smsRequest
import com.messaging.provider.SendRequest
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

@DisplayName("AbstractHttpProvider - HTTP 기반 Provider 공통 동작")
class AbstractHttpProviderTest : MockWebServerTestBase() {

    private fun createTestProvider(
        timeout: Duration = Duration.ofSeconds(5),
        maxRetries: Int = 3,
        customHeaders: Map<String, Any?> = emptyMap()
    ): TestHttpProvider {
        return TestHttpProvider(webClient, circuitBreaker, timeout, maxRetries, customHeaders)
    }

    @Nested
    @DisplayName("응답 처리 - 벤더 응답을 SendResult로 변환")
    inner class ResponseMappingTest {

        @Test
        @DisplayName("성공 응답 -> SendResult.success")
        fun `성공 응답 처리`() {
            // Given
            enqueue(mockSuccess("""{"success": true, "message": "OK"}"""))

            // When
            val result = createTestProvider().send(smsRequest()).block()

            // Then
            result.shouldBeSuccess()
            result shouldHaveMessage "OK"
        }

        @Test
        @DisplayName("비즈니스 실패 응답 -> SendResult.fail (코드/메시지 포함)")
        fun `실패 응답 처리`() {
            // Given
            enqueue(mockSuccess("""{"success": false, "code": "E001", "message": "실패"}"""))

            // When
            val result = createTestProvider().send(smsRequest()).block()

            // Then
            result.shouldBeFail(expectedCode = "E001", expectedMessage = "실패")
        }
    }

    @Nested
    @DisplayName("HTTP 에러 처리 - 4xx/5xx 응답")
    inner class HttpErrorTest {

        @Test
        @DisplayName("HTTP 400 -> HTTP_400 에러 코드")
        fun `HTTP 400 에러 처리`() {
            // Given
            enqueue(mockHttpError(400))

            // When
            val result = createTestProvider().send(smsRequest()).block()

            // Then
            result.shouldBeFail()
            result shouldHaveCode "HTTP_400"
        }

        @Test
        @DisplayName("HTTP 500 -> HTTP_500 에러 코드")
        fun `HTTP 500 에러 처리`() {
            // Given
            enqueue(mockHttpError(500))

            // When
            val result = createTestProvider().send(smsRequest()).block()

            // Then
            result.shouldBeFail()
            result shouldHaveCode "HTTP_500"
        }
    }

    @Nested
    @DisplayName("네트워크 에러 처리 - 타임아웃/연결 실패")
    inner class NetworkErrorTest {

        @Test
        @DisplayName("타임아웃 -> TIMEOUT 에러 코드 + retryable=true")
        fun `타임아웃 처리`() {
            // Given
            enqueue(mockTimeout(delaySeconds = 3))

            // When
            val result = createTestProvider(timeout = Duration.ofSeconds(1))
                .send(smsRequest())
                .block()

            // Then
            result.shouldBeFail()
            result shouldHaveCode "TIMEOUT"
            result.shouldBeRetryable()
        }

        @Test
        @DisplayName("연결 실패 후 재시도 성공 -> 최종 성공")
        fun `연결 실패 시 재시도 후 성공`() {
            // Given
            enqueue(mockDisconnect())
            enqueue(mockSuccess("""{"success": true, "message": "OK"}"""))

            // When
            val result = createTestProvider().send(smsRequest()).block()

            // Then
            result.shouldBeSuccess()
            assertEquals(2, requestCount())
        }

        @Test
        @DisplayName("연결 실패 maxRetries 초과 -> CONNECTION_ERROR + retryable=true")
        fun `연결 실패 최대 재시도 초과`() {
            // Given
            repeat(5) { enqueue(mockDisconnect()) }

            // When
            val result = createTestProvider(maxRetries = 2).send(smsRequest()).block()

            // Then
            result.shouldBeFail()
            result shouldHaveCode "CONNECTION_ERROR"
            result.shouldBeRetryable()
        }
    }

    @Nested
    @DisplayName("서킷브레이커 - 장애 격리")
    inner class CircuitBreakerTest {

        @Test
        @DisplayName("서킷 OPEN 상태 -> CIRCUIT_OPEN 에러 코드 + retryable=true")
        fun `서킷브레이커 OPEN 상태 처리`() {
            // Given
            circuitBreaker.transitionToOpenState()

            // When
            val result = createTestProvider().send(smsRequest()).block()

            // Then
            result.shouldBeFail()
            result shouldHaveCode "CIRCUIT_OPEN"
            result.shouldBeRetryable()
        }
    }

    @Nested
    @DisplayName("요청 헤더 처리")
    inner class RequestHeaderTest {

        @Test
        @DisplayName("Content-Type: application/json 헤더가 기본 설정된다")
        fun `요청 헤더 전달 확인`() {
            // Given
            enqueue(mockSuccess("""{"success": true, "message": "OK"}"""))

            // When
            createTestProvider().send(smsRequest()).block()

            // Then
            val recordedRequest = takeRequest()
            assertEquals("application/json", recordedRequest.getHeader("Content-Type"))
        }

        @Test
        @DisplayName("커스텀 헤더에 null 값이 있으면 해당 헤더는 제외된다")
        fun `null 헤더 값 처리`() {
            // Given
            enqueue(mockSuccess("""{"success": true, "message": "OK"}"""))
            val customHeaders = mapOf<String, Any?>(
                "X-Valid-Header" to "valid-value",
                "X-Null-Header" to null
            )

            // When
            val result = createTestProvider(customHeaders = customHeaders).send(smsRequest()).block()

            // Then
            result.shouldBeSuccess()
            val recordedRequest = takeRequest()
            assertEquals("valid-value", recordedRequest.getHeader("X-Valid-Header"))
        }
    }

    /**
     * 테스트용 Provider 구현
     */
    class TestHttpProvider(
        webClient: WebClient,
        circuitBreaker: CircuitBreaker,
        private val testTimeout: Duration,
        private val testMaxRetries: Int,
        private val customHeaders: Map<String, Any?> = emptyMap()
    ) : AbstractHttpProvider<TestHttpProvider.TestResponse>(
        webClient, circuitBreaker, TestResponse::class.java
    ) {
        override val logPrefix = "TEST"
        override val endpoint = "/api/test"
        override val timeout: Duration get() = testTimeout
        override val maxRetries: Int get() = testMaxRetries

        override fun supportedTypes() = setOf(MessageType.SMS)
        override fun supportedCarrier(): Carrier = Carrier.SKT

        override fun buildVendorRequest(request: SendRequest) = VendorRequest(
            body = mapOf("msgId" to request.messageId) + request.detail,
            headers = customHeaders
        )

        override fun mapToSendResult(response: TestResponse): SendResult {
            return if (response.success) {
                SendResult.success(response.message ?: "성공")
            } else {
                SendResult.fail(response.code ?: "UNKNOWN", response.message ?: "실패")
            }
        }

        data class TestResponse(
            val success: Boolean = false,
            val code: String? = null,
            val message: String? = null
        )
    }
}
