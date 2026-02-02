package com.messaging.bootstrap.rcs.receiver.handler

import com.messaging.infrastructure.netty.protocol.ProtocolMessage
import com.messaging.usecase.rcs.RcsAuthUseCase
import io.netty.channel.ChannelHandlerContext
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class AuthHandler(
    private val objectMapper: ObjectMapper,
    private val rcsAuthUseCase: RcsAuthUseCase
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun handle(ctx: ChannelHandlerContext, msg: ProtocolMessage) {
        val request = try {
            objectMapper.readValue(msg.body, AuthRequest::class.java)
        } catch (e: Exception) {
            log.error("Failed to parse auth request: {}", e.message)
            val response = ProtocolMessage.authNack(
                msg.header.sequence,
                """{"code":"4000","message":"Invalid request format"}"""
            )
            ctx.writeAndFlush(response)
            return
        }

        runBlocking {
            val result = rcsAuthUseCase.authenticate(request.partnerId, request.secretKey)

            if (result.success) {
                ctx.channel().attr(RcsServerHandler.AUTHENTICATED).set(true)
                ctx.channel().attr(RcsServerHandler.PARTNER_ID).set(request.partnerId)

                val response = ProtocolMessage.authAck(msg.header.sequence)
                ctx.writeAndFlush(response)
            } else {
                val response = ProtocolMessage.authNack(
                    msg.header.sequence,
                    """{"code":"${result.errorCode}","message":"${result.errorMessage}"}"""
                )
                ctx.writeAndFlush(response)
            }
        }
    }
}

data class AuthRequest(
    val partnerId: String,
    val secretKey: String
)
