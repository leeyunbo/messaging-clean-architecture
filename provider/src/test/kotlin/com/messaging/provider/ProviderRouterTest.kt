package com.messaging.provider

import com.messaging.common.domain.Carrier
import com.messaging.common.domain.MessageType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

@DisplayName("ProviderRouter - 메시지 타입/통신사 기반 Provider 라우팅")
class ProviderRouterTest {

    private lateinit var router: ProviderRouter
    private lateinit var smsProvider: FakeProvider
    private lateinit var kakaoProvider: FakeProvider

    @BeforeEach
    fun setup() {
        smsProvider = FakeProvider(
            types = setOf(MessageType.SMS),
            carrier = Carrier.SKT
        )
        kakaoProvider = FakeProvider(
            types = setOf(MessageType.KAKAO_ALIMTALK),
            carrier = null
        )
        router = ProviderRouter(listOf(smsProvider, kakaoProvider))
    }

    @Nested
    @DisplayName("라우팅 - 메시지 타입과 통신사에 맞는 Provider로 요청 전달")
    inner class RoutingTest {

        @Test
        @DisplayName("SMS + SKT 요청 -> SKT SMS Provider로 라우팅")
        fun `SMS SKT 요청을 올바른 Provider로 라우팅`() {
            // When
            val result = router.send(smsRequest()).block()

            // Then
            result.shouldBeSuccess()
            assertTrue(smsProvider.wasCalled)
            assertFalse(kakaoProvider.wasCalled)
        }

        @Test
        @DisplayName("카카오 알림톡 요청 -> Kakao Provider로 라우팅")
        fun `카카오 알림톡 요청을 올바른 Provider로 라우팅`() {
            // When
            val result = router.send(kakaoAlimtalkRequest()).block()

            // Then
            result.shouldBeSuccess()
            assertFalse(smsProvider.wasCalled)
            assertTrue(kakaoProvider.wasCalled)
        }
    }

    @Nested
    @DisplayName("에러 처리 - 지원하지 않는 요청에 대한 실패 응답")
    inner class ErrorHandlingTest {

        @Test
        @DisplayName("지원하지 않는 메시지 타입 -> P001 에러 코드 반환")
        fun `지원하지 않는 메시지 타입 요청 시 에러 반환`() {
            // Given - LMS Provider 없음
            val request = lmsRequest()

            // When
            val result = router.send(request).block()

            // Then
            result.shouldBeFail(expectedCode = "P001")
            result shouldContainMessage "지원하지 않는"
        }

        @Test
        @DisplayName("지원하지 않는 통신사 -> P001 에러 코드 반환")
        fun `지원하지 않는 통신사 요청 시 에러 반환`() {
            // Given - KT SMS Provider 없음
            val request = smsRequest(carrier = Carrier.KT)

            // When
            val result = router.send(request).block()

            // Then
            result.shouldBeFail(expectedCode = "P001")
        }
    }

    @Nested
    @DisplayName("Provider 조회 - getProvider 메서드")
    inner class GetProviderTest {

        @Test
        @DisplayName("SMS + SKT -> SMS Provider 반환")
        fun `SMS SKT Provider 조회`() {
            assertEquals(smsProvider, router.getProvider(MessageType.SMS, Carrier.SKT))
        }

        @Test
        @DisplayName("카카오 알림톡 -> Kakao Provider 반환")
        fun `카카오 알림톡 Provider 조회`() {
            assertEquals(kakaoProvider, router.getProvider(MessageType.KAKAO_ALIMTALK, null))
        }

        @Test
        @DisplayName("지원하지 않는 조합 -> null 반환")
        fun `지원하지 않는 조합 조회 시 null 반환`() {
            assertNull(router.getProvider(MessageType.LMS, Carrier.SKT))
        }
    }

    /**
     * 테스트용 Fake Provider
     */
    class FakeProvider(
        private val types: Set<MessageType>,
        private val carrier: Carrier?
    ) : MessageProvider {
        var wasCalled = false

        override fun supportedTypes() = types
        override fun supportedCarrier() = carrier

        override fun send(request: SendRequest): Mono<SendResult> {
            wasCalled = true
            return Mono.just(SendResult.success("Fake 성공"))
        }
    }
}
