package com.messaging.bootstrap.rcs.receiver.handler

import com.messaging.infrastructure.netty.protocol.ProtocolMessage
import com.messaging.usecase.rcs.RcsReceiveRequest
import com.messaging.usecase.rcs.RcsReceiveUseCase
import io.netty.channel.ChannelHandlerContext
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ObjectMapper

@Component
class SendHandler(
    private val objectMapper: ObjectMapper,
    private val rcsReceiveUseCase: RcsReceiveUseCase
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun handle(ctx: ChannelHandlerContext, msg: ProtocolMessage) {
        val partnerId = ctx.channel().attr(RcsServerHandler.PARTNER_ID).get()

        val request = try {
            objectMapper.readValue(msg.body, SendRequestDto::class.java)
        } catch (e: Exception) {
            log.error("Failed to parse send request: {}", e.message)
            sendNack(ctx, msg.header.sequence, "4000", "Invalid request format")
            return
        }

        runBlocking {
            val receiveRequest = RcsReceiveRequest(
                partnerId = partnerId,
                type = request.type,
                recipient = request.recipient,
                content = request.content,
                buttons = request.buttons?.toString(),
                cards = request.cards?.toString()
            )

            val result = rcsReceiveUseCase.receive(receiveRequest)

            if (result.success) {
                val body = objectMapper.writeValueAsString(SendAckResponse(result.messageId!!))
                ctx.writeAndFlush(ProtocolMessage.sendAck(msg.header.sequence, body))
            } else {
                sendNack(ctx, msg.header.sequence, result.errorCode ?: "5000", result.errorMessage ?: "Send failed")
            }
        }
    }

    private fun sendNack(ctx: ChannelHandlerContext, sequence: Long, code: String, message: String) {
        val body = objectMapper.writeValueAsString(ErrorResponse(code, message))
        ctx.writeAndFlush(ProtocolMessage.sendNack(sequence, body))
    }
}

data class SendRequestDto(
    val type: String,
    val recipient: String,
    val content: String? = null,
    val buttons: JsonNode? = null,
    val cards: JsonNode? = null
)

data class SendAckResponse(
    val messageId: String
)
