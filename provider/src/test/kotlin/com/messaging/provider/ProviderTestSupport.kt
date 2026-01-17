package com.messaging.provider

import com.messaging.common.domain.Carrier
import com.messaging.common.domain.MessageType
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.http.codec.json.JacksonJsonDecoder
import org.springframework.http.codec.json.JacksonJsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import java.time.Duration
import java.util.concurrent.TimeUnit

object ProviderTestSupport {

    fun createJsonMapper(): JsonMapper = JsonMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .build()

    fun createWebClient(mockServer: MockWebServer): WebClient {
        val jsonMapper = createJsonMapper()
        val strategies = ExchangeStrategies.builder()
            .codecs { configurer ->
                configurer.defaultCodecs().jacksonJsonEncoder(JacksonJsonEncoder(jsonMapper))
                configurer.defaultCodecs().jacksonJsonDecoder(JacksonJsonDecoder(jsonMapper))
            }
            .build()

        return WebClient.builder()
            .baseUrl(mockServer.url("/").toString())
            .exchangeStrategies(strategies)
            .build()
    }

    fun createCircuitBreaker(name: String = "test"): CircuitBreaker {
        val config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50f)
            .slidingWindowSize(10)
            .minimumNumberOfCalls(5)
            .waitDurationInOpenState(Duration.ofSeconds(1))
            .permittedNumberOfCallsInHalfOpenState(3)
            .build()

        return CircuitBreakerRegistry.of(config).circuitBreaker(name)
    }

    fun createCircuitBreakerForTest(name: String = "test"): CircuitBreaker {
        // 테스트용: 최소 호출 1번, 실패율 100%면 바로 OPEN
        val config = CircuitBreakerConfig.custom()
            .failureRateThreshold(100f)
            .slidingWindowSize(1)
            .minimumNumberOfCalls(1)
            .waitDurationInOpenState(Duration.ofSeconds(60))
            .permittedNumberOfCallsInHalfOpenState(1)
            .build()

        return CircuitBreakerRegistry.of(config).circuitBreaker(name)
    }
}

// ============================================
// MockResponse 헬퍼 함수
// ============================================

/**
 * 성공 응답 MockResponse 생성
 */
fun mockSuccess(body: String = """{"success": true, "message": "OK"}"""): MockResponse =
    MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "application/json")
        .setBody(body)

/**
 * 실패 응답 MockResponse 생성 (HTTP 200이지만 비즈니스 에러)
 */
fun mockError(body: String, statusCode: Int = 200): MockResponse =
    MockResponse()
        .setResponseCode(statusCode)
        .setHeader("Content-Type", "application/json")
        .setBody(body)

/**
 * HTTP 에러 응답 MockResponse 생성
 */
fun mockHttpError(statusCode: Int): MockResponse =
    MockResponse().setResponseCode(statusCode)

/**
 * 타임아웃 시뮬레이션 MockResponse 생성
 */
fun mockTimeout(delaySeconds: Long = 10): MockResponse =
    MockResponse()
        .setBodyDelay(delaySeconds, TimeUnit.SECONDS)
        .setResponseCode(200)
        .setHeader("Content-Type", "application/json")
        .setBody("""{"success": true}""")

/**
 * 연결 끊김 시뮬레이션
 */
fun mockDisconnect(): MockResponse =
    MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)

// ============================================
// Provider별 응답 생성 헬퍼
// ============================================

/** SKT 형식 응답 (resultCode/resultMsg) */
object SktResponse {
    fun success(message: String = "발송 성공") =
        mockSuccess("""{"resultCode": "0000", "resultMsg": "$message"}""")

    fun error(code: String = "E001", message: String = "발송 실패") =
        mockSuccess("""{"resultCode": "$code", "resultMsg": "$message"}""")

    fun successNoMsg() = mockSuccess("""{"resultCode": "0000"}""")
    fun errorNoMsg(code: String = "E001") = mockSuccess("""{"resultCode": "$code"}""")
}

/** LGT 형식 응답 (code/description) */
object LgtResponse {
    fun success(message: String = "성공") =
        mockSuccess("""{"code": "SUCCESS", "description": "$message"}""")

    fun error(code: String = "E001", message: String = "실패") =
        mockSuccess("""{"code": "$code", "description": "$message"}""")

    fun successNoMsg() = mockSuccess("""{"code": "SUCCESS"}""")
    fun errorNoMsg(code: String = "E001") = mockSuccess("""{"code": "$code"}""")
}

/** KT 형식 응답 (status/message) */
object KtResponse {
    fun success(message: String = "성공") =
        mockSuccess("""{"status": 0, "message": "$message"}""")

    fun error(message: String = "실패") =
        mockSuccess("""{"status": 1, "message": "$message"}""")

    fun successNoMsg() = mockSuccess("""{"status": 0}""")
    fun errorNoMsg() = mockSuccess("""{"status": 1}""")
}

/** Kakao 형식 응답 (resultCode: Int/resultMessage) */
object KakaoResponse {
    fun success(message: String = "성공") =
        mockSuccess("""{"resultCode": 0, "resultMessage": "$message"}""")

    fun error(message: String = "실패") =
        mockSuccess("""{"resultCode": 1, "resultMessage": "$message"}""")

    fun successNoMsg() = mockSuccess("""{"resultCode": 0}""")
    fun errorNoMsg() = mockSuccess("""{"resultCode": 1}""")
}

/** Naver 형식 응답 (success: Boolean/message) */
object NaverResponse {
    fun success(message: String = "성공") =
        mockSuccess("""{"success": true, "message": "$message"}""")

    fun error(code: String = "E001", message: String = "실패") =
        mockSuccess("""{"success": false, "errorCode": "$code", "message": "$message"}""")

    fun successNoMsg() = mockSuccess("""{"success": true}""")
    fun errorNoMsg(code: String = "E001") = mockSuccess("""{"success": false, "errorCode": "$code"}""")
}

// ============================================
// SendRequest 빌더 DSL
// ============================================

@DslMarker
annotation class SendRequestDsl

@SendRequestDsl
class SendRequestBuilder {
    var messageId: String = "test-msg-${System.nanoTime()}"
    var messageType: MessageType = MessageType.SMS
    var carrier: Carrier? = Carrier.SKT
    var recipient: String = "01012345678"
    var content: String = "테스트 메시지"
    private var detailMap: MutableMap<String, Any?> = mutableMapOf("callback" to "01011112222")

    fun detail(vararg pairs: Pair<String, Any?>) {
        detailMap.putAll(pairs)
    }

    fun clearDetail() {
        detailMap.clear()
    }

    fun build(): SendRequest = SendRequest(
        messageId = messageId,
        messageType = messageType,
        carrier = carrier,
        recipient = recipient,
        content = content,
        detail = detailMap.toMap()
    )
}

/** 테스트용 SendRequest 생성 DSL */
fun sendRequest(block: SendRequestBuilder.() -> Unit = {}): SendRequest =
    SendRequestBuilder().apply(block).build()

/** SMS 요청 간편 생성 */
fun smsRequest(carrier: Carrier = Carrier.SKT, block: SendRequestBuilder.() -> Unit = {}): SendRequest =
    sendRequest {
        messageType = MessageType.SMS
        this.carrier = carrier
        block()
    }

/** LMS 요청 간편 생성 */
fun lmsRequest(carrier: Carrier = Carrier.SKT, block: SendRequestBuilder.() -> Unit = {}): SendRequest =
    sendRequest {
        messageType = MessageType.LMS
        this.carrier = carrier
        block()
    }

/** MMS 요청 간편 생성 */
fun mmsRequest(carrier: Carrier = Carrier.SKT, block: SendRequestBuilder.() -> Unit = {}): SendRequest =
    sendRequest {
        messageType = MessageType.MMS
        this.carrier = carrier
        block()
    }

/** RCS 요청 간편 생성 */
fun rcsRequest(carrier: Carrier = Carrier.SKT, block: SendRequestBuilder.() -> Unit = {}): SendRequest =
    sendRequest {
        messageType = MessageType.RCS
        this.carrier = carrier
        block()
    }

/** 카카오 알림톡 요청 간편 생성 */
fun kakaoAlimtalkRequest(block: SendRequestBuilder.() -> Unit = {}): SendRequest =
    sendRequest {
        messageType = MessageType.KAKAO_ALIMTALK
        carrier = null
        block()
    }

/** 카카오 브랜드메시지 요청 간편 생성 */
fun kakaoBrandRequest(block: SendRequestBuilder.() -> Unit = {}): SendRequest =
    sendRequest {
        messageType = MessageType.KAKAO_BRAND_MESSAGE
        carrier = null
        block()
    }

/** 네이버톡 요청 간편 생성 */
fun naverTalkRequest(block: SendRequestBuilder.() -> Unit = {}): SendRequest =
    sendRequest {
        messageType = MessageType.NAVER_TALK
        carrier = null
        block()
    }

// ============================================
// Assertion 헬퍼 (확장 함수)
// ============================================

/** SendResult 성공 검증 */
fun SendResult?.shouldBeSuccess(messageCheck: ((String) -> Unit)? = null) {
    assertNotNull(this) { "SendResult가 null입니다" }
    assertTrue(this!!.success) { "결과가 실패입니다: ${this.resultCode} - ${this.resultMessage}" }
    messageCheck?.invoke(this.resultMessage)
}

/** SendResult 실패 검증 */
fun SendResult?.shouldBeFail(expectedCode: String? = null, expectedMessage: String? = null) {
    assertNotNull(this) { "SendResult가 null입니다" }
    assertFalse(this!!.success) { "결과가 성공입니다: ${this.resultMessage}" }
    expectedCode?.let {
        assertEquals(it, this.resultCode) { "예상 코드: $it, 실제: ${this.resultCode}" }
    }
    expectedMessage?.let {
        assertEquals(it, this.resultMessage) { "예상 메시지: $it, 실제: ${this.resultMessage}" }
    }
}

/** 재시도 가능 여부 검증 */
fun SendResult?.shouldBeRetryable() {
    assertNotNull(this) { "SendResult가 null입니다" }
    assertTrue(this!!.retryable) { "결과가 retryable이 아닙니다: ${this.resultCode}" }
}

fun SendResult?.shouldNotBeRetryable() {
    assertNotNull(this) { "SendResult가 null입니다" }
    assertFalse(this!!.retryable) { "결과가 retryable입니다: ${this.resultCode}" }
}

/** 결과 코드 검증 (infix) */
infix fun SendResult?.shouldHaveCode(expectedCode: String) {
    assertNotNull(this) { "SendResult가 null입니다" }
    assertEquals(expectedCode, this!!.resultCode) { "예상 코드: $expectedCode, 실제: ${this.resultCode}" }
}

/** 결과 메시지 검증 (infix) */
infix fun SendResult?.shouldHaveMessage(expectedMessage: String) {
    assertNotNull(this) { "SendResult가 null입니다" }
    assertEquals(expectedMessage, this!!.resultMessage) { "예상 메시지: $expectedMessage, 실제: ${this.resultMessage}" }
}

/** 결과 메시지 포함 검증 (infix) */
infix fun SendResult?.shouldContainMessage(substring: String) {
    assertNotNull(this) { "SendResult가 null입니다" }
    assertTrue(this!!.resultMessage.contains(substring)) {
        "메시지에 '$substring'이 포함되어야 합니다. 실제: ${this.resultMessage}"
    }
}
