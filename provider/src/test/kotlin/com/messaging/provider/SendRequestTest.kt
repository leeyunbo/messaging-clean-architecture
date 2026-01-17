package com.messaging.provider

import com.messaging.common.domain.Carrier
import com.messaging.common.domain.MessageType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SendRequestTest {

    @Test
    fun `기본 생성`() {
        val request = SendRequest(
            messageId = "msg-001",
            messageType = MessageType.SMS,
            carrier = Carrier.SKT,
            recipient = "01012345678",
            content = "테스트"
        )

        assertEquals("msg-001", request.messageId)
        assertEquals(MessageType.SMS, request.messageType)
        assertEquals(Carrier.SKT, request.carrier)
        assertEquals("01012345678", request.recipient)
        assertEquals("테스트", request.content)
        assertTrue(request.detail.isEmpty())
    }

    @Test
    fun `carrier null 허용`() {
        val request = SendRequest(
            messageId = "msg-001",
            messageType = MessageType.KAKAO_ALIMTALK,
            carrier = null,
            recipient = "01012345678",
            content = "테스트"
        )

        assertNull(request.carrier)
    }

    @Test
    fun `detail에서 값 조회 - getDetail`() {
        val request = SendRequest(
            messageId = "msg-001",
            messageType = MessageType.SMS,
            carrier = Carrier.SKT,
            recipient = "01012345678",
            content = "테스트",
            detail = mapOf(
                "callback" to "01011112222",
                "count" to 10,
                "nested" to mapOf("key" to "value")
            )
        )

        assertEquals("01011112222", request.getDetail<String>("callback"))
        assertEquals(10, request.getDetail<Int>("count"))
        assertEquals(mapOf("key" to "value"), request.getDetail<Map<String, String>>("nested"))
    }

    @Test
    fun `detail에서 존재하지 않는 키 조회 시 null 반환`() {
        val request = SendRequest(
            messageId = "msg-001",
            messageType = MessageType.SMS,
            carrier = Carrier.SKT,
            recipient = "01012345678",
            content = "테스트",
            detail = emptyMap()
        )

        assertNull(request.getDetail<String>("nonexistent"))
    }

    @Test
    fun `detail에서 null 값 조회`() {
        val request = SendRequest(
            messageId = "msg-001",
            messageType = MessageType.SMS,
            carrier = Carrier.SKT,
            recipient = "01012345678",
            content = "테스트",
            detail = mapOf("nullField" to null)
        )

        assertNull(request.getDetail<String>("nullField"))
    }
}
