package com.messaging.provider.http

import com.messaging.common.domain.Carrier
import com.messaging.common.domain.MessageType
import com.messaging.provider.MessageProvider
import com.messaging.provider.ProviderTestSupport
import com.messaging.provider.http.kakao.KakaoAlimtalkProvider
import com.messaging.provider.http.kakao.KakaoBrandMessageProvider
import com.messaging.provider.http.lmsmms.KtLmsMmsProvider
import com.messaging.provider.http.lmsmms.LgtLmsMmsProvider
import com.messaging.provider.http.lmsmms.SktLmsMmsProvider
import com.messaging.provider.http.naver.NaverTalkProvider
import com.messaging.provider.http.rcs.KtRcsProvider
import com.messaging.provider.http.rcs.LgtRcsProvider
import com.messaging.provider.http.rcs.SktRcsProvider
import com.messaging.provider.http.sms.KtSmsProvider
import com.messaging.provider.http.sms.LgtSmsProvider
import com.messaging.provider.http.sms.SktSmsProvider
import com.messaging.provider.mockSuccess
import com.messaging.provider.sendRequest
import com.messaging.provider.shouldBeFail
import com.messaging.provider.shouldBeSuccess
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * ========================================
 * Provider 모듈 기능 명세
 * ========================================
 *
 * 이 테스트는 12개의 메시지 Provider가 올바르게 동작하는지 검증합니다.
 *
 * 지원하는 메시지 타입:
 * - SMS: SKT, KT, LGT
 * - LMS/MMS: SKT, KT, LGT
 * - RCS: SKT, KT, LGT
 * - 카카오 알림톡
 * - 카카오 브랜드메시지
 * - 네이버톡톡
 *
 * 각 Provider는 다음 기능을 제공합니다:
 * - 메시지 발송 (성공/실패 응답 처리)
 * - 메시지 타입 지원 여부 확인
 * - 통신사 지원 여부 확인
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Provider 통합 테스트")
class AllProvidersTest {

    private lateinit var mockServer: MockWebServer

    @BeforeAll
    fun setup() {
        mockServer = MockWebServer()
        mockServer.start()
    }

    @AfterAll
    fun teardown() {
        mockServer.shutdown()
    }

    private fun enqueue(body: String) = mockServer.enqueue(mockSuccess(body))

    // ========================================
    // SMS Provider 테스트
    // ========================================

    @Nested
    @DisplayName("SMS Provider - 단문 메시지 발송")
    inner class SmsProviderTest {

        @Nested
        @DisplayName("SKT SMS")
        inner class SktSms {
            private fun provider() = SktSmsProvider(
                ProviderTestSupport.createWebClient(mockServer),
                ProviderTestSupport.createCircuitBreaker("skt-sms-${System.nanoTime()}")
            )

            @Test
            fun `SMS 타입을 지원한다`() {
                assertEquals(setOf(MessageType.SMS), provider().supportedTypes())
            }

            @Test
            fun `SKT 통신사를 지원한다`() {
                assertEquals(Carrier.SKT, provider().supportedCarrier())
            }

            @Test
            fun `발송 성공 시 success=true를 반환한다`() {
                enqueue("""{"resultCode": "0000", "resultMsg": "성공"}""")
                provider().send(sendRequest()).block().shouldBeSuccess()
            }

            @Test
            fun `발송 실패 시 success=false를 반환한다`() {
                enqueue("""{"resultCode": "E001", "resultMsg": "실패"}""")
                provider().send(sendRequest()).block().shouldBeFail()
            }
        }

        @Nested
        @DisplayName("KT SMS")
        inner class KtSms {
            private fun provider() = KtSmsProvider(
                ProviderTestSupport.createWebClient(mockServer),
                ProviderTestSupport.createCircuitBreaker("kt-sms-${System.nanoTime()}")
            )

            @Test
            fun `SMS 타입을 지원한다`() {
                assertEquals(setOf(MessageType.SMS), provider().supportedTypes())
            }

            @Test
            fun `KT 통신사를 지원한다`() {
                assertEquals(Carrier.KT, provider().supportedCarrier())
            }

            @Test
            fun `발송 성공 시 success=true를 반환한다`() {
                enqueue("""{"status": 0, "message": "성공"}""")
                provider().send(sendRequest()).block().shouldBeSuccess()
            }

            @Test
            fun `발송 실패 시 success=false를 반환한다`() {
                enqueue("""{"status": 1, "message": "실패"}""")
                provider().send(sendRequest()).block().shouldBeFail()
            }
        }

        @Nested
        @DisplayName("LGT SMS")
        inner class LgtSms {
            private fun provider() = LgtSmsProvider(
                ProviderTestSupport.createWebClient(mockServer),
                ProviderTestSupport.createCircuitBreaker("lgt-sms-${System.nanoTime()}")
            )

            @Test
            fun `SMS 타입을 지원한다`() {
                assertEquals(setOf(MessageType.SMS), provider().supportedTypes())
            }

            @Test
            fun `LGT 통신사를 지원한다`() {
                assertEquals(Carrier.LGT, provider().supportedCarrier())
            }

            @Test
            fun `발송 성공 시 success=true를 반환한다`() {
                enqueue("""{"code": "SUCCESS", "description": "성공"}""")
                provider().send(sendRequest()).block().shouldBeSuccess()
            }

            @Test
            fun `발송 실패 시 success=false를 반환한다`() {
                enqueue("""{"code": "E001", "description": "실패"}""")
                provider().send(sendRequest()).block().shouldBeFail()
            }
        }
    }

    // ========================================
    // LMS/MMS Provider 테스트
    // ========================================

    @Nested
    @DisplayName("LMS/MMS Provider - 장문/멀티미디어 메시지 발송")
    inner class LmsMmsProviderTest {

        @Nested
        @DisplayName("SKT LMS/MMS")
        inner class SktLmsMms {
            private fun provider() = SktLmsMmsProvider(
                ProviderTestSupport.createWebClient(mockServer),
                ProviderTestSupport.createCircuitBreaker("skt-lmsmms-${System.nanoTime()}")
            )

            @Test
            fun `LMS와 MMS 타입을 지원한다`() {
                assertEquals(setOf(MessageType.LMS, MessageType.MMS), provider().supportedTypes())
            }

            @Test
            fun `SKT 통신사를 지원한다`() {
                assertEquals(Carrier.SKT, provider().supportedCarrier())
            }

            @Test
            fun `발송 성공 시 success=true를 반환한다`() {
                enqueue("""{"resultCode": "0000", "resultMsg": "성공"}""")
                provider().send(sendRequest { messageType = MessageType.LMS }).block().shouldBeSuccess()
            }

            @Test
            fun `발송 실패 시 success=false를 반환한다`() {
                enqueue("""{"resultCode": "E001", "resultMsg": "실패"}""")
                provider().send(sendRequest { messageType = MessageType.LMS }).block().shouldBeFail()
            }
        }

        @Nested
        @DisplayName("KT LMS/MMS")
        inner class KtLmsMms {
            private fun provider() = KtLmsMmsProvider(
                ProviderTestSupport.createWebClient(mockServer),
                ProviderTestSupport.createCircuitBreaker("kt-lmsmms-${System.nanoTime()}")
            )

            @Test
            fun `LMS와 MMS 타입을 지원한다`() {
                assertEquals(setOf(MessageType.LMS, MessageType.MMS), provider().supportedTypes())
            }

            @Test
            fun `KT 통신사를 지원한다`() {
                assertEquals(Carrier.KT, provider().supportedCarrier())
            }

            @Test
            fun `발송 성공 시 success=true를 반환한다`() {
                enqueue("""{"status": 0, "message": "성공"}""")
                provider().send(sendRequest { messageType = MessageType.LMS }).block().shouldBeSuccess()
            }

            @Test
            fun `발송 실패 시 success=false를 반환한다`() {
                enqueue("""{"status": 1, "message": "실패"}""")
                provider().send(sendRequest { messageType = MessageType.LMS }).block().shouldBeFail()
            }
        }

        @Nested
        @DisplayName("LGT LMS/MMS")
        inner class LgtLmsMms {
            private fun provider() = LgtLmsMmsProvider(
                ProviderTestSupport.createWebClient(mockServer),
                ProviderTestSupport.createCircuitBreaker("lgt-lmsmms-${System.nanoTime()}")
            )

            @Test
            fun `LMS와 MMS 타입을 지원한다`() {
                assertEquals(setOf(MessageType.LMS, MessageType.MMS), provider().supportedTypes())
            }

            @Test
            fun `LGT 통신사를 지원한다`() {
                assertEquals(Carrier.LGT, provider().supportedCarrier())
            }

            @Test
            fun `발송 성공 시 success=true를 반환한다`() {
                enqueue("""{"code": "SUCCESS", "description": "성공"}""")
                provider().send(sendRequest { messageType = MessageType.LMS }).block().shouldBeSuccess()
            }

            @Test
            fun `발송 실패 시 success=false를 반환한다`() {
                enqueue("""{"code": "E001", "description": "실패"}""")
                provider().send(sendRequest { messageType = MessageType.LMS }).block().shouldBeFail()
            }
        }
    }

    // ========================================
    // RCS Provider 테스트
    // ========================================

    @Nested
    @DisplayName("RCS Provider - 리치 메시지 발송")
    inner class RcsProviderTest {

        @Nested
        @DisplayName("SKT RCS")
        inner class SktRcs {
            private fun provider() = SktRcsProvider(
                ProviderTestSupport.createWebClient(mockServer),
                ProviderTestSupport.createCircuitBreaker("skt-rcs-${System.nanoTime()}")
            )

            @Test
            fun `RCS 타입을 지원한다`() {
                assertEquals(setOf(MessageType.RCS), provider().supportedTypes())
            }

            @Test
            fun `SKT 통신사를 지원한다`() {
                assertEquals(Carrier.SKT, provider().supportedCarrier())
            }

            @Test
            fun `발송 성공 시 success=true를 반환한다`() {
                enqueue("""{"resultCode": "0000", "resultMsg": "성공"}""")
                provider().send(sendRequest { messageType = MessageType.RCS }).block().shouldBeSuccess()
            }

            @Test
            fun `발송 실패 시 success=false를 반환한다`() {
                enqueue("""{"resultCode": "E001", "resultMsg": "실패"}""")
                provider().send(sendRequest { messageType = MessageType.RCS }).block().shouldBeFail()
            }
        }

        @Nested
        @DisplayName("KT RCS")
        inner class KtRcs {
            private fun provider() = KtRcsProvider(
                ProviderTestSupport.createWebClient(mockServer),
                ProviderTestSupport.createCircuitBreaker("kt-rcs-${System.nanoTime()}")
            )

            @Test
            fun `RCS 타입을 지원한다`() {
                assertEquals(setOf(MessageType.RCS), provider().supportedTypes())
            }

            @Test
            fun `KT 통신사를 지원한다`() {
                assertEquals(Carrier.KT, provider().supportedCarrier())
            }

            @Test
            fun `발송 성공 시 success=true를 반환한다`() {
                enqueue("""{"status": 0, "message": "성공"}""")
                provider().send(sendRequest { messageType = MessageType.RCS }).block().shouldBeSuccess()
            }

            @Test
            fun `발송 실패 시 success=false를 반환한다`() {
                enqueue("""{"status": 1, "message": "실패"}""")
                provider().send(sendRequest { messageType = MessageType.RCS }).block().shouldBeFail()
            }
        }

        @Nested
        @DisplayName("LGT RCS")
        inner class LgtRcs {
            private fun provider() = LgtRcsProvider(
                ProviderTestSupport.createWebClient(mockServer),
                ProviderTestSupport.createCircuitBreaker("lgt-rcs-${System.nanoTime()}")
            )

            @Test
            fun `RCS 타입을 지원한다`() {
                assertEquals(setOf(MessageType.RCS), provider().supportedTypes())
            }

            @Test
            fun `LGT 통신사를 지원한다`() {
                assertEquals(Carrier.LGT, provider().supportedCarrier())
            }

            @Test
            fun `발송 성공 시 success=true를 반환한다`() {
                enqueue("""{"code": "SUCCESS", "description": "성공"}""")
                provider().send(sendRequest { messageType = MessageType.RCS }).block().shouldBeSuccess()
            }

            @Test
            fun `발송 실패 시 success=false를 반환한다`() {
                enqueue("""{"code": "E001", "description": "실패"}""")
                provider().send(sendRequest { messageType = MessageType.RCS }).block().shouldBeFail()
            }
        }
    }

    // ========================================
    // 카카오 Provider 테스트
    // ========================================

    @Nested
    @DisplayName("카카오 Provider - 알림톡/브랜드메시지 발송")
    inner class KakaoProviderTest {

        @Nested
        @DisplayName("카카오 알림톡")
        inner class KakaoAlimtalk {
            private fun provider() = KakaoAlimtalkProvider(
                ProviderTestSupport.createWebClient(mockServer),
                ProviderTestSupport.createCircuitBreaker("kakao-alimtalk-${System.nanoTime()}")
            )

            @Test
            fun `KAKAO_ALIMTALK 타입을 지원한다`() {
                assertEquals(setOf(MessageType.KAKAO_ALIMTALK), provider().supportedTypes())
            }

            @Test
            fun `통신사 구분이 없다 (null)`() {
                assertEquals(null, provider().supportedCarrier())
            }

            @Test
            fun `발송 성공 시 success=true를 반환한다`() {
                enqueue("""{"resultCode": 0, "resultMessage": "성공"}""")
                provider().send(sendRequest {
                    messageType = MessageType.KAKAO_ALIMTALK
                    carrier = null
                }).block().shouldBeSuccess()
            }

            @Test
            fun `발송 실패 시 success=false를 반환한다`() {
                enqueue("""{"resultCode": 1, "resultMessage": "실패"}""")
                provider().send(sendRequest {
                    messageType = MessageType.KAKAO_ALIMTALK
                    carrier = null
                }).block().shouldBeFail()
            }
        }

        @Nested
        @DisplayName("카카오 브랜드메시지")
        inner class KakaoBrandMessage {
            private fun provider() = KakaoBrandMessageProvider(
                ProviderTestSupport.createWebClient(mockServer),
                ProviderTestSupport.createCircuitBreaker("kakao-brand-${System.nanoTime()}")
            )

            @Test
            fun `KAKAO_BRAND_MESSAGE 타입을 지원한다`() {
                assertEquals(setOf(MessageType.KAKAO_BRAND_MESSAGE), provider().supportedTypes())
            }

            @Test
            fun `통신사 구분이 없다 (null)`() {
                assertEquals(null, provider().supportedCarrier())
            }

            @Test
            fun `발송 성공 시 success=true를 반환한다`() {
                enqueue("""{"resultCode": 0, "resultMessage": "성공"}""")
                provider().send(sendRequest {
                    messageType = MessageType.KAKAO_BRAND_MESSAGE
                    carrier = null
                }).block().shouldBeSuccess()
            }

            @Test
            fun `발송 실패 시 success=false를 반환한다`() {
                enqueue("""{"resultCode": 1, "resultMessage": "실패"}""")
                provider().send(sendRequest {
                    messageType = MessageType.KAKAO_BRAND_MESSAGE
                    carrier = null
                }).block().shouldBeFail()
            }
        }
    }

    // ========================================
    // 네이버 Provider 테스트
    // ========================================

    @Nested
    @DisplayName("네이버 Provider - 네이버톡톡 발송")
    inner class NaverProviderTest {

        @Nested
        @DisplayName("네이버톡톡")
        inner class NaverTalk {
            private fun provider() = NaverTalkProvider(
                ProviderTestSupport.createWebClient(mockServer),
                ProviderTestSupport.createCircuitBreaker("naver-talk-${System.nanoTime()}")
            )

            @Test
            fun `NAVER_TALK 타입을 지원한다`() {
                assertEquals(setOf(MessageType.NAVER_TALK), provider().supportedTypes())
            }

            @Test
            fun `통신사 구분이 없다 (null)`() {
                assertEquals(null, provider().supportedCarrier())
            }

            @Test
            fun `발송 성공 시 success=true를 반환한다`() {
                enqueue("""{"success": true, "message": "성공"}""")
                provider().send(sendRequest {
                    messageType = MessageType.NAVER_TALK
                    carrier = null
                }).block().shouldBeSuccess()
            }

            @Test
            fun `발송 실패 시 success=false를 반환한다`() {
                enqueue("""{"success": false, "errorCode": "E001", "message": "실패"}""")
                provider().send(sendRequest {
                    messageType = MessageType.NAVER_TALK
                    carrier = null
                }).block().shouldBeFail()
            }
        }
    }
}
