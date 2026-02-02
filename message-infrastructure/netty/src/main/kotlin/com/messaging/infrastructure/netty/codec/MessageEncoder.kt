package com.messaging.infrastructure.netty.codec

import com.messaging.infrastructure.netty.protocol.MessageHeader
import com.messaging.infrastructure.netty.protocol.ProtocolMessage
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class MessageEncoder : MessageToByteEncoder<ProtocolMessage>() {

    override fun encode(ctx: ChannelHandlerContext, msg: ProtocolMessage, out: ByteBuf) {
        val bodyBytes = msg.body.toByteArray(Charsets.UTF_8)

        // STX
        out.writeByte(MessageHeader.STX.toInt())

        // TYPE (2바이트)
        out.writeBytes(msg.header.type.code.toByteArray(Charsets.UTF_8))

        // LENGTH (4바이트)
        out.writeInt(bodyBytes.size)

        // SEQ (8바이트)
        out.writeLong(msg.header.sequence)

        // RESERVED (4바이트)
        out.writeZero(4)

        // ETX
        out.writeByte(MessageHeader.ETX.toInt())

        // BODY
        out.writeBytes(bodyBytes)
    }
}
