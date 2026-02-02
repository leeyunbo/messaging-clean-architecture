package com.messaging.infrastructure.netty.codec

import com.messaging.infrastructure.netty.protocol.ProtocolMessage
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class MessageEncoder : MessageToByteEncoder<ProtocolMessage>() {

    override fun encode(ctx: ChannelHandlerContext, msg: ProtocolMessage, out: ByteBuf) {
        val bodyBytes = msg.body.toByteArray(Charsets.UTF_8)

        // 헤더 직렬화 (MessageHeader에 위임)
        msg.header.writeTo(out, bodyBytes.size)

        // BODY
        out.writeBytes(bodyBytes)
    }
}
