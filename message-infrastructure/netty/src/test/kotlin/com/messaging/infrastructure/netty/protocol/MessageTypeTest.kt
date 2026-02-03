package com.messaging.infrastructure.netty.protocol

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MessageTypeTest {

    @Test
    fun `fromCode - AUTH 타입 반환`() {
        assertEquals(MessageType.AUTH, MessageType.fromCode("AU"))
    }

    @Test
    fun `fromCode - AUTH_ACK 타입 반환`() {
        assertEquals(MessageType.AUTH_ACK, MessageType.fromCode("AA"))
    }

    @Test
    fun `fromCode - AUTH_NACK 타입 반환`() {
        assertEquals(MessageType.AUTH_NACK, MessageType.fromCode("AN"))
    }

    @Test
    fun `fromCode - SEND 타입 반환`() {
        assertEquals(MessageType.SEND, MessageType.fromCode("SR"))
    }

    @Test
    fun `fromCode - SEND_ACK 타입 반환`() {
        assertEquals(MessageType.SEND_ACK, MessageType.fromCode("SA"))
    }

    @Test
    fun `fromCode - SEND_NACK 타입 반환`() {
        assertEquals(MessageType.SEND_NACK, MessageType.fromCode("SN"))
    }

    @Test
    fun `fromCode - 알 수 없는 코드는 null 반환`() {
        assertNull(MessageType.fromCode("XX"))
        assertNull(MessageType.fromCode(""))
        assertNull(MessageType.fromCode("A"))
        assertNull(MessageType.fromCode("AUX"))
    }

    @Test
    fun `code 속성 확인`() {
        assertEquals("AU", MessageType.AUTH.code)
        assertEquals("AA", MessageType.AUTH_ACK.code)
        assertEquals("AN", MessageType.AUTH_NACK.code)
        assertEquals("SR", MessageType.SEND.code)
        assertEquals("SA", MessageType.SEND_ACK.code)
        assertEquals("SN", MessageType.SEND_NACK.code)
    }

    @Test
    fun `모든 타입의 코드는 2자리`() {
        MessageType.entries.forEach { type ->
            assertEquals(2, type.code.length, "${type.name}의 코드 길이가 2가 아님")
        }
    }

    @Test
    fun `모든 타입의 코드는 고유함`() {
        val codes = MessageType.entries.map { it.code }
        assertEquals(codes.size, codes.toSet().size, "중복된 코드 존재")
    }
}
