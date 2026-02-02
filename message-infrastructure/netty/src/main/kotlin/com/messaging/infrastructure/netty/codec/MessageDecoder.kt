package com.messaging.infrastructure.netty.codec

import com.messaging.infrastructure.netty.protocol.MessageHeader
import com.messaging.infrastructure.netty.protocol.ProtocolMessage
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import org.slf4j.LoggerFactory

class MessageDecoder : ByteToMessageDecoder() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, out: MutableList<Any>) {
        // 헤더 역직렬화 (MessageHeader에 위임)
        when (val result = MessageHeader.readFrom(buf)) {
            is MessageHeader.ReadResult.InsufficientData -> return
            is MessageHeader.ReadResult.InvalidStx -> {
                log.warn("Invalid STX: {}", result.actual)
                return
            }
            is MessageHeader.ReadResult.InvalidEtx -> {
                log.warn("Invalid ETX: {}", result.actual)
                return
            }
            is MessageHeader.ReadResult.InvalidType -> {
                log.warn("Unknown message type: {}", result.typeCode)
                return
            }
            is MessageHeader.ReadResult.Success -> {
                val header = result.header

                // BODY가 충분히 있는지 확인
                if (buf.readableBytes() < header.length) {
                    buf.resetReaderIndex()
                    return
                }

                // BODY 읽기
                val bodyBytes = ByteArray(header.length)
                buf.readBytes(bodyBytes)
                val body = String(bodyBytes, Charsets.UTF_8)

                out.add(ProtocolMessage(header, body))
            }
        }
    }
}
