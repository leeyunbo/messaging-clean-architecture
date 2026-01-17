package com.messaging.provider

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.web.reactive.function.client.WebClient

/**
 * MockWebServer 기반 테스트 베이스 클래스
 *
 * 사용 예:
 * ```
 * class SktSmsProviderTest : MockWebServerTestBase() {
 *     private lateinit var provider: SktSmsProvider
 *
 *     override fun createProvider() {
 *         provider = SktSmsProvider(webClient, circuitBreaker)
 *     }
 *
 *     @Test
 *     fun `성공 응답 처리`() {
 *         // Given
 *         enqueue(SktResponse.success())
 *
 *         // When
 *         val result = provider.send(smsRequest()).block()
 *
 *         // Then
 *         result.shouldBeSuccess()
 *     }
 * }
 * ```
 */
abstract class MockWebServerTestBase {

    protected lateinit var mockServer: MockWebServer
    protected lateinit var webClient: WebClient
    protected lateinit var circuitBreaker: CircuitBreaker

    /**
     * Provider 생성 훅 - 서브클래스에서 구현
     */
    protected open fun createProvider() {}

    /**
     * 추가 설정 훅 - 서브클래스에서 필요시 오버라이드
     */
    protected open fun additionalSetup() {}

    @BeforeEach
    fun setupMockServer() {
        mockServer = MockWebServer()
        mockServer.start()
        webClient = ProviderTestSupport.createWebClient(mockServer)
        circuitBreaker = ProviderTestSupport.createCircuitBreaker("test-${System.nanoTime()}")
        createProvider()
        additionalSetup()
    }

    @AfterEach
    fun teardownMockServer() {
        mockServer.shutdown()
    }

    /**
     * MockResponse 큐에 추가
     */
    protected fun enqueue(response: MockResponse) {
        mockServer.enqueue(response)
    }

    /**
     * 여러 MockResponse 큐에 추가
     */
    protected fun enqueueAll(vararg responses: MockResponse) {
        responses.forEach { mockServer.enqueue(it) }
    }

    /**
     * 성공 응답 큐에 추가 (편의 메서드)
     */
    protected fun enqueueSuccess(body: String = """{"success": true, "message": "OK"}""") {
        enqueue(mockSuccess(body))
    }

    /**
     * 실패 응답 큐에 추가 (편의 메서드)
     */
    protected fun enqueueError(body: String, statusCode: Int = 200) {
        enqueue(mockError(body, statusCode))
    }

    /**
     * HTTP 에러 응답 큐에 추가 (편의 메서드)
     */
    protected fun enqueueHttpError(statusCode: Int) {
        enqueue(mockHttpError(statusCode))
    }

    /**
     * 마지막 요청 가져오기
     */
    protected fun takeRequest() = mockServer.takeRequest()

    /**
     * 요청 본문 가져오기
     */
    protected fun takeRequestBody(): String = takeRequest().body.readUtf8()

    /**
     * 요청 개수 확인
     */
    protected fun requestCount(): Int = mockServer.requestCount
}

/**
 * 빠른 서킷브레이커 테스트용 베이스 클래스
 * (서킷브레이커가 1회 실패로 OPEN되도록 설정)
 */
abstract class FastCircuitBreakerTestBase : MockWebServerTestBase() {

    override fun additionalSetup() {
        circuitBreaker = ProviderTestSupport.createCircuitBreakerForTest("test-fast-${System.nanoTime()}")
    }
}
