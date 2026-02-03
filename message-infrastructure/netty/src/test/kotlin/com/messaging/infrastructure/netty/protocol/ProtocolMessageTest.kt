package com.messaging.infrastructure.netty.protocol

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ProtocolMessageTest {

    @Test
    fun `auth 팩토리 메서드`() {
        val body = """{"partnerId":"test","secretKey":"secret"}"""
        val message = ProtocolMessage.auth(1L, body)

        assertEquals(MessageType.AUTH, message.header.type)
        assertEquals(1L, message.header.sequence)
        assertEquals(body.toByteArray(Charsets.UTF_8).size, message.header.length)
        assertEquals(body, message.body)
    }

    @Test
    fun `authAck 팩토리 메서드`() {
        val message = ProtocolMessage.authAck(42L)

        assertEquals(MessageType.AUTH_ACK, message.header.type)
        assertEquals(42L, message.header.sequence)
        assertEquals(0, message.header.length)
        assertEquals("", message.body)
    }

    @Test
    fun `authNack 팩토리 메서드`() {
        val body = """{"code":"4001","message":"Invalid credentials"}"""
        val message = ProtocolMessage.authNack(100L, body)

        assertEquals(MessageType.AUTH_NACK, message.header.type)
        assertEquals(100L, message.header.sequence)
        assertEquals(body.toByteArray(Charsets.UTF_8).size, message.header.length)
        assertEquals(body, message.body)
    }

    @Test
    fun `send 팩토리 메서드`() {
        val body = """{"type":"SMS","recipient":"01012345678","content":"테스트"}"""
        val message = ProtocolMessage.send(50L, body)

        assertEquals(MessageType.SEND, message.header.type)
        assertEquals(50L, message.header.sequence)
        assertEquals(body.toByteArray(Charsets.UTF_8).size, message.header.length)
        assertEquals(body, message.body)
    }

    @Test
    fun `sendAck 팩토리 메서드`() {
        val body = """{"messageId":"msg-001"}"""
        val message = ProtocolMessage.sendAck(200L, body)

        assertEquals(MessageType.SEND_ACK, message.header.type)
        assertEquals(200L, message.header.sequence)
        assertEquals(body.toByteArray(Charsets.UTF_8).size, message.header.length)
        assertEquals(body, message.body)
    }

    @Test
    fun `sendNack 팩토리 메서드`() {
        val body = """{"code":"5000","message":"Internal error"}"""
        val message = ProtocolMessage.sendNack(300L, body)

        assertEquals(MessageType.SEND_NACK, message.header.type)
        assertEquals(300L, message.header.sequence)
        assertEquals(body.toByteArray(Charsets.UTF_8).size, message.header.length)
        assertEquals(body, message.body)
    }

    @Test
    fun `한글 바디 length 계산 - UTF8 바이트 기준`() {
        val body = """{"content":"안녕하세요"}"""
        val message = ProtocolMessage.send(1L, body)

        // UTF-8에서 한글은 3바이트
        val expectedLength = body.toByteArray(Charsets.UTF_8).size
        assertEquals(expectedLength, message.header.length)
        assertTrue(message.header.length > body.length, "UTF-8 인코딩 시 한글은 문자열 길이보다 바이트 크기가 커야 함")
    }

    @Test
    fun `빈 바디 메시지`() {
        val message = ProtocolMessage.send(1L, "")

        assertEquals(0, message.header.length)
        assertEquals("", message.body)
    }

    @Test
    fun `data class equals 동작 확인`() {
        val body = """{"test":"value"}"""
        val message1 = ProtocolMessage.send(1L, body)
        val message2 = ProtocolMessage.send(1L, body)

        assertEquals(message1, message2)
    }

    @Test
    fun `data class hashCode 동작 확인`() {
        val body = """{"test":"value"}"""
        val message1 = ProtocolMessage.send(1L, body)
        val message2 = ProtocolMessage.send(1L, body)

        assertEquals(message1.hashCode(), message2.hashCode())
    }
}
