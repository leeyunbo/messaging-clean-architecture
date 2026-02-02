package com.messaging.bootstrap.rcs.receiver.handler

import com.messaging.infrastructure.netty.protocol.MessageType
import com.messaging.infrastructure.netty.protocol.ProtocolMessage
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.AttributeKey
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Sharable
@Component
class RcsServerHandler(
    private val authHandler: AuthHandler,
    private val sendHandler: SendHandler
) : SimpleChannelInboundHandler<ProtocolMessage>() {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        val AUTHENTICATED = AttributeKey.valueOf<Boolean>("authenticated")
        val PARTNER_ID = AttributeKey.valueOf<String>("partnerId")
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        log.info("Client connected: {}", ctx.channel().remoteAddress())
        ctx.channel().attr(AUTHENTICATED).set(false)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        log.info("Client disconnected: {}", ctx.channel().remoteAddress())
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ProtocolMessage) {
        log.debug("Received message: type={}, seq={}", msg.header.type, msg.header.sequence)

        when (msg.header.type) {
            MessageType.AUTH -> authHandler.handle(ctx, msg)
            MessageType.SEND -> {
                if (isAuthenticated(ctx)) {
                    sendHandler.handle(ctx, msg)
                } else {
                    log.warn("Unauthenticated request from: {}", ctx.channel().remoteAddress())
                    val response = ProtocolMessage.sendNack(
                        msg.header.sequence,
                        """{"code":"4010","message":"Not authenticated"}"""
                    )
                    ctx.writeAndFlush(response)
                }
            }
            else -> {
                log.warn("Unexpected message type: {}", msg.header.type)
            }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        log.error("Exception in channel: {}", cause.message, cause)
        ctx.close()
    }

    private fun isAuthenticated(ctx: ChannelHandlerContext): Boolean {
        return ctx.channel().attr(AUTHENTICATED).get() == true
    }
}
