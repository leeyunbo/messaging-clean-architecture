package com.messaging.infrastructure.netty.codec

import com.messaging.infrastructure.netty.protocol.MessageHeader
import com.messaging.infrastructure.netty.protocol.MessageType
import com.messaging.infrastructure.netty.protocol.ProtocolMessage
import io.netty.buffer.ByteBuf
import io.netty.channel.embedded.EmbeddedChannel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MessageEncoderTest {

    private lateinit var channel: EmbeddedChannel

    @BeforeEach
    fun setup() {
        channel = EmbeddedChannel(MessageEncoder())
    }

    @AfterEach
    fun teardown() {
        channel.finishAndReleaseAll()
    }

    @Test
    fun `AUTH 메시지 인코딩`() {
        val body = """{"partnerId":"test","secretKey":"secret"}"""
        val message = ProtocolMessage.auth(1L, body)

        channel.writeOutbound(message)

        val encoded = channel.readOutbound<ByteBuf>()
        assertNotNull(encoded)

        // STX 확인
        assertEquals(MessageHeader.STX, encoded.readByte())

        // TYPE 확인
        val typeBytes = ByteArray(2)
        encoded.readBytes(typeBytes)
        assertEquals("AU", String(typeBytes))

        // LENGTH 확인
        val length = encoded.readInt()
        assertEquals(body.toByteArray().size, length)

        // SEQUENCE 확인
        assertEquals(1L, encoded.readLong())

        // RESERVED 스킵
        encoded.skipBytes(4)

        // ETX 확인
        assertEquals(MessageHeader.ETX, encoded.readByte())

        // BODY 확인
        val bodyBytes = ByteArray(length)
        encoded.readBytes(bodyBytes)
        assertEquals(body, String(bodyBytes))

        encoded.release()
    }

    @Test
    fun `SEND 메시지 인코딩`() {
        val body = """{"type":"SMS","recipient":"01012345678","content":"테스트"}"""
        val message = ProtocolMessage.send(100L, body)

        channel.writeOutbound(message)

        val encoded = channel.readOutbound<ByteBuf>()
        assertNotNull(encoded)

        assertEquals(MessageHeader.STX, encoded.readByte())

        val typeBytes = ByteArray(2)
        encoded.readBytes(typeBytes)
        assertEquals("SR", String(typeBytes))

        val length = encoded.readInt()
        assertEquals(body.toByteArray(Charsets.UTF_8).size, length)

        assertEquals(100L, encoded.readLong())

        encoded.skipBytes(4)

        assertEquals(MessageHeader.ETX, encoded.readByte())

        val bodyBytes = ByteArray(length)
        encoded.readBytes(bodyBytes)
        assertEquals(body, String(bodyBytes, Charsets.UTF_8))

        encoded.release()
    }

    @Test
    fun `AUTH_ACK 메시지 인코딩 - 빈 바디`() {
        val message = ProtocolMessage.authAck(42L)

        channel.writeOutbound(message)

        val encoded = channel.readOutbound<ByteBuf>()
        assertNotNull(encoded)

        assertEquals(MessageHeader.STX, encoded.readByte())

        val typeBytes = ByteArray(2)
        encoded.readBytes(typeBytes)
        assertEquals("AA", String(typeBytes))

        val length = encoded.readInt()
        assertEquals(0, length)

        assertEquals(42L, encoded.readLong())

        encoded.skipBytes(4)

        assertEquals(MessageHeader.ETX, encoded.readByte())

        assertEquals(0, encoded.readableBytes())

        encoded.release()
    }

    @Test
    fun `한글 본문 인코딩`() {
        val body = """{"content":"안녕하세요 테스트입니다"}"""
        val message = ProtocolMessage.send(1L, body)

        channel.writeOutbound(message)

        val encoded = channel.readOutbound<ByteBuf>()
        assertNotNull(encoded)

        encoded.skipBytes(3) // STX + TYPE

        val length = encoded.readInt()
        assertEquals(body.toByteArray(Charsets.UTF_8).size, length)

        encoded.skipBytes(13) // SEQ + RESERVED + ETX

        val bodyBytes = ByteArray(length)
        encoded.readBytes(bodyBytes)
        assertEquals(body, String(bodyBytes, Charsets.UTF_8))

        encoded.release()
    }

    @Test
    fun `전체 헤더 크기 확인`() {
        val message = ProtocolMessage.authAck(1L)

        channel.writeOutbound(message)

        val encoded = channel.readOutbound<ByteBuf>()
        assertNotNull(encoded)

        // 헤더: STX(1) + TYPE(2) + LENGTH(4) + SEQ(8) + RESERVED(4) + ETX(1) = 20 bytes
        assertEquals(MessageHeader.HEADER_SIZE, encoded.readableBytes())

        encoded.release()
    }
}
