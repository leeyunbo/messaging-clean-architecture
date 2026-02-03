package com.messaging.infrastructure.netty.codec

import com.messaging.infrastructure.netty.protocol.MessageHeader
import com.messaging.infrastructure.netty.protocol.MessageType
import com.messaging.infrastructure.netty.protocol.ProtocolMessage
import io.netty.buffer.Unpooled
import io.netty.channel.embedded.EmbeddedChannel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MessageDecoderTest {

    private lateinit var channel: EmbeddedChannel

    @BeforeEach
    fun setup() {
        channel = EmbeddedChannel(MessageDecoder())
    }

    @AfterEach
    fun teardown() {
        channel.finishAndReleaseAll()
    }

    @Test
    fun `AUTH 메시지 디코딩`() {
        val body = """{"partnerId":"test","secretKey":"secret"}"""
        val buf = createProtocolBuffer("AU", 1L, body)

        channel.writeInbound(buf)

        val decoded = channel.readInbound<ProtocolMessage>()
        assertNotNull(decoded)
        assertEquals(MessageType.AUTH, decoded.header.type)
        assertEquals(1L, decoded.header.sequence)
        assertEquals(body, decoded.body)
    }

    @Test
    fun `SEND 메시지 디코딩`() {
        val body = """{"type":"SMS","recipient":"01012345678","content":"테스트"}"""
        val buf = createProtocolBuffer("SR", 100L, body)

        channel.writeInbound(buf)

        val decoded = channel.readInbound<ProtocolMessage>()
        assertNotNull(decoded)
        assertEquals(MessageType.SEND, decoded.header.type)
        assertEquals(100L, decoded.header.sequence)
        assertEquals(body, decoded.body)
    }

    @Test
    fun `AUTH_ACK 디코딩 - 빈 바디`() {
        val buf = createProtocolBuffer("AA", 42L, "")

        channel.writeInbound(buf)

        val decoded = channel.readInbound<ProtocolMessage>()
        assertNotNull(decoded)
        assertEquals(MessageType.AUTH_ACK, decoded.header.type)
        assertEquals(42L, decoded.header.sequence)
        assertEquals("", decoded.body)
    }

    @Test
    fun `AUTH_NACK 디코딩`() {
        val body = """{"code":"4001","message":"Invalid credentials"}"""
        val buf = createProtocolBuffer("AN", 1L, body)

        channel.writeInbound(buf)

        val decoded = channel.readInbound<ProtocolMessage>()
        assertNotNull(decoded)
        assertEquals(MessageType.AUTH_NACK, decoded.header.type)
        assertEquals(body, decoded.body)
    }

    @Test
    fun `SEND_ACK 디코딩`() {
        val body = """{"messageId":"msg-001"}"""
        val buf = createProtocolBuffer("SA", 50L, body)

        channel.writeInbound(buf)

        val decoded = channel.readInbound<ProtocolMessage>()
        assertNotNull(decoded)
        assertEquals(MessageType.SEND_ACK, decoded.header.type)
        assertEquals(50L, decoded.header.sequence)
        assertEquals(body, decoded.body)
    }

    @Test
    fun `SEND_NACK 디코딩`() {
        val body = """{"code":"5000","message":"Internal error"}"""
        val buf = createProtocolBuffer("SN", 50L, body)

        channel.writeInbound(buf)

        val decoded = channel.readInbound<ProtocolMessage>()
        assertNotNull(decoded)
        assertEquals(MessageType.SEND_NACK, decoded.header.type)
        assertEquals(body, decoded.body)
    }

    @Test
    fun `불완전한 헤더 - 데이터 부족`() {
        val buf = Unpooled.buffer()
        buf.writeByte(MessageHeader.STX.toInt())
        buf.writeBytes("AU".toByteArray())
        // 헤더 불완전

        channel.writeInbound(buf)

        val decoded = channel.readInbound<ProtocolMessage>()
        assertNull(decoded)
    }

    @Test
    fun `불완전한 바디 - 데이터 부족`() {
        val bodyBytes = "partial".toByteArray()
        val buf = Unpooled.buffer()
        buf.writeByte(MessageHeader.STX.toInt())
        buf.writeBytes("SR".toByteArray())
        buf.writeInt(100) // LENGTH가 100이지만 실제 바디는 7바이트
        buf.writeLong(1L)
        buf.writeZero(4)
        buf.writeByte(MessageHeader.ETX.toInt())
        buf.writeBytes(bodyBytes)

        channel.writeInbound(buf)

        val decoded = channel.readInbound<ProtocolMessage>()
        assertNull(decoded)
    }

    @Test
    fun `잘못된 STX - 스킵`() {
        val buf = Unpooled.buffer()
        buf.writeByte(0x00) // 잘못된 STX
        buf.writeBytes("AU".toByteArray())

        channel.writeInbound(buf)

        val decoded = channel.readInbound<ProtocolMessage>()
        assertNull(decoded)
    }

    @Test
    fun `잘못된 ETX - 스킵`() {
        val buf = Unpooled.buffer()
        buf.writeByte(MessageHeader.STX.toInt())
        buf.writeBytes("AU".toByteArray())
        buf.writeInt(0)
        buf.writeLong(1L)
        buf.writeZero(4)
        buf.writeByte(0x00) // 잘못된 ETX

        channel.writeInbound(buf)

        val decoded = channel.readInbound<ProtocolMessage>()
        assertNull(decoded)
    }

    @Test
    fun `알 수 없는 메시지 타입 - 스킵`() {
        val buf = Unpooled.buffer()
        buf.writeByte(MessageHeader.STX.toInt())
        buf.writeBytes("XX".toByteArray()) // 알 수 없는 타입
        buf.writeInt(0)
        buf.writeLong(1L)
        buf.writeZero(4)
        buf.writeByte(MessageHeader.ETX.toInt())

        channel.writeInbound(buf)

        val decoded = channel.readInbound<ProtocolMessage>()
        assertNull(decoded)
    }

    @Test
    fun `한글 본문 디코딩`() {
        val body = """{"content":"안녕하세요 테스트입니다"}"""
        val buf = createProtocolBuffer("SR", 1L, body)

        channel.writeInbound(buf)

        val decoded = channel.readInbound<ProtocolMessage>()
        assertNotNull(decoded)
        assertEquals(body, decoded.body)
    }

    @Test
    fun `연속된 메시지 디코딩`() {
        val body1 = """{"seq":1}"""
        val body2 = """{"seq":2}"""

        val buf = Unpooled.buffer()
        writeProtocolMessage(buf, "SR", 1L, body1)
        writeProtocolMessage(buf, "SR", 2L, body2)

        channel.writeInbound(buf)

        val decoded1 = channel.readInbound<ProtocolMessage>()
        assertNotNull(decoded1)
        assertEquals(1L, decoded1.header.sequence)
        assertEquals(body1, decoded1.body)

        val decoded2 = channel.readInbound<ProtocolMessage>()
        assertNotNull(decoded2)
        assertEquals(2L, decoded2.header.sequence)
        assertEquals(body2, decoded2.body)
    }

    @Test
    fun `분할 수신 시뮬레이션`() {
        val body = """{"partnerId":"test"}"""
        val bodyBytes = body.toByteArray(Charsets.UTF_8)

        // 헤더 일부만 전송
        val buf1 = Unpooled.buffer()
        buf1.writeByte(MessageHeader.STX.toInt())
        buf1.writeBytes("AU".toByteArray())
        channel.writeInbound(buf1)
        assertNull(channel.readInbound<ProtocolMessage>())

        // 나머지 헤더 전송
        val buf2 = Unpooled.buffer()
        buf2.writeInt(bodyBytes.size)
        buf2.writeLong(1L)
        buf2.writeZero(4)
        buf2.writeByte(MessageHeader.ETX.toInt())
        channel.writeInbound(buf2)
        assertNull(channel.readInbound<ProtocolMessage>())

        // 바디 전송
        val buf3 = Unpooled.buffer()
        buf3.writeBytes(bodyBytes)
        channel.writeInbound(buf3)

        val decoded = channel.readInbound<ProtocolMessage>()
        assertNotNull(decoded)
        assertEquals(MessageType.AUTH, decoded.header.type)
        assertEquals(body, decoded.body)
    }

    private fun createProtocolBuffer(type: String, sequence: Long, body: String): io.netty.buffer.ByteBuf {
        val buf = Unpooled.buffer()
        writeProtocolMessage(buf, type, sequence, body)
        return buf
    }

    private fun writeProtocolMessage(buf: io.netty.buffer.ByteBuf, type: String, sequence: Long, body: String) {
        val bodyBytes = body.toByteArray(Charsets.UTF_8)
        buf.writeByte(MessageHeader.STX.toInt())
        buf.writeBytes(type.toByteArray(Charsets.UTF_8))
        buf.writeInt(bodyBytes.size)
        buf.writeLong(sequence)
        buf.writeZero(4)
        buf.writeByte(MessageHeader.ETX.toInt())
        buf.writeBytes(bodyBytes)
    }
}
