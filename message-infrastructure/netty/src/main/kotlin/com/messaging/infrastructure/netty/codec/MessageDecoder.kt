package com.messaging.infrastructure.netty.codec

import com.messaging.infrastructure.netty.protocol.MessageHeader
import com.messaging.infrastructure.netty.protocol.MessageType
import com.messaging.infrastructure.netty.protocol.ProtocolMessage
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import org.slf4j.LoggerFactory

class MessageDecoder : ByteToMessageDecoder() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        // 헤더 크기만큼 데이터가 있는지 확인
        if (buf.readableBytes() < MessageHeader.HEADER_SIZE) {
            return
        }

        buf.markReaderIndex()

        // STX 확인
        val stx = buf.readByte()
        if (stx != MessageHeader.STX) {
            log.warn("Invalid STX: {}", stx)
            buf.resetReaderIndex()
            buf.skipBytes(1)
            return
        }

        // TYPE (2바이트)
        val typeBytes = ByteArray(2)
        buf.readBytes(typeBytes)
        val typeCode = String(typeBytes, Charsets.UTF_8)
        val type = MessageType.fromCode(typeCode)
        if (type == null) {
            log.warn("Unknown message type: {}", typeCode)
            buf.resetReaderIndex()
            buf.skipBytes(1)
            return
        }

        // LENGTH (4바이트)
        val length = buf.readInt()

        // SEQ (8바이트)
        val sequence = buf.readLong()

        // RESERVED (4바이트)
        buf.skipBytes(4)

        // ETX 확인
        val etx = buf.readByte()
        if (etx != MessageHeader.ETX) {
            log.warn("Invalid ETX: {}", etx)
            buf.resetReaderIndex()
            buf.skipBytes(1)
            return
        }

        // BODY가 충분히 있는지 확인
        if (buf.readableBytes() < length) {
            buf.resetReaderIndex()
            return
        }

        // BODY 읽기
        val bodyBytes = ByteArray(length)
        buf.readBytes(bodyBytes)
        val body = String(bodyBytes, Charsets.UTF_8)

        val header = MessageHeader(type, length, sequence)
        val message = ProtocolMessage(header, body)

        out.add(message)
    }
}
