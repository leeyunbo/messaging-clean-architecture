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
            sendNack(ctx, msg.header.sequence, "4000", "Invalid request format")
            return
        }

        runBlocking {
            val result = rcsAuthUseCase.authenticate(request.partnerId, request.secretKey)

            if (result.success) {
                ctx.channel().attr(RcsServerHandler.AUTHENTICATED).set(true)
                ctx.channel().attr(RcsServerHandler.PARTNER_ID).set(request.partnerId)
                ctx.writeAndFlush(ProtocolMessage.authAck(msg.header.sequence))
            } else {
                sendNack(ctx, msg.header.sequence, result.errorCode ?: "5000", result.errorMessage ?: "Authentication failed")
            }
        }
    }

    private fun sendNack(ctx: ChannelHandlerContext, sequence: Long, code: String, message: String) {
        val body = objectMapper.writeValueAsString(ErrorResponse(code, message))
        ctx.writeAndFlush(ProtocolMessage.authNack(sequence, body))
    }
}

data class AuthRequest(
    val partnerId: String,
    val secretKey: String
)

data class ErrorResponse(
    val code: String,
    val message: String
)
