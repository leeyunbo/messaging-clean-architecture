package com.messaging.provider.http.sms

import com.messaging.common.domain.Carrier
import com.messaging.common.domain.MessageType
import com.messaging.provider.MockWebServerTestBase
import com.messaging.provider.SktResponse
import com.messaging.provider.shouldBeFail
import com.messaging.provider.shouldBeSuccess
import com.messaging.provider.shouldHaveMessage
import com.messaging.provider.smsRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("SktSmsProvider - SKT SMS 발송 Provider")
class SktSmsProviderTest : MockWebServerTestBase() {

    private lateinit var provider: SktSmsProvider

    override fun createProvider() {
        provider = SktSmsProvider(webClient, circuitBreaker)
    }

    @Nested
    @DisplayName("Provider 메타정보")
    inner class MetadataTest {

        @Test
        @DisplayName("SMS 메시지 타입을 지원한다")
        fun `supportedTypes는 SMS 반환`() {
            assertEquals(setOf(MessageType.SMS), provider.supportedTypes())
        }

        @Test
        @DisplayName("SKT 통신사를 지원한다")
        fun `supportedCarrier는 SKT 반환`() {
            assertEquals(Carrier.SKT, provider.supportedCarrier())
        }
    }

    @Nested
    @DisplayName("발송 결과 처리")
    inner class SendResultTest {

        @Test
        @DisplayName("resultCode=0000 -> 성공 응답")
        fun `성공 응답 - resultCode 0000`() {
            // Given
            enqueue(SktResponse.success("발송 성공"))

            // When
            val result = provider.send(smsRequest()).block()

            // Then
            result.shouldBeSuccess()
            result shouldHaveMessage "발송 성공"
        }

        @Test
        @DisplayName("resultCode!=0000 -> 실패 응답 (에러 코드/메시지 포함)")
        fun `실패 응답 - 에러 코드 반환`() {
            // Given
            enqueue(SktResponse.error("S002", "수신자 번호 오류"))

            // When
            val result = provider.send(smsRequest()).block()

            // Then
            result.shouldBeFail(expectedCode = "S002", expectedMessage = "수신자 번호 오류")
        }

        @Test
        @DisplayName("resultMsg가 null인 성공 응답 -> 기본 메시지 사용")
        fun `성공 응답 - resultMsg가 null인 경우 기본 메시지 사용`() {
            // Given
            enqueue(SktResponse.successNoMsg())

            // When
            val result = provider.send(smsRequest()).block()

            // Then
            result.shouldBeSuccess()
            result shouldHaveMessage "발송 성공"
        }

        @Test
        @DisplayName("resultMsg가 null인 실패 응답 -> 기본 메시지 사용")
        fun `실패 응답 - resultMsg가 null인 경우 기본 메시지 사용`() {
            // Given
            enqueue(SktResponse.errorNoMsg("E999"))

            // When
            val result = provider.send(smsRequest()).block()

            // Then
            result.shouldBeFail(expectedCode = "E999", expectedMessage = "발송 실패")
        }
    }

    @Nested
    @DisplayName("요청 바디 생성")
    inner class RequestBuildingTest {

        @Test
        @DisplayName("msgId, recipient, detail 필드가 요청 바디에 포함된다")
        fun `buildVendorRequest - msgId와 detail 포함 확인`() {
            // Given
            enqueue(SktResponse.success())
            val request = smsRequest {
                messageId = "test-123"
                detail("customField" to "value")
            }

            // When
            provider.send(request).block()

            // Then
            val body = takeRequestBody()
            assertTrue(body.contains("test-123"))
            assertTrue(body.contains("01011112222"))
            assertTrue(body.contains("customField"))
        }
    }
}
