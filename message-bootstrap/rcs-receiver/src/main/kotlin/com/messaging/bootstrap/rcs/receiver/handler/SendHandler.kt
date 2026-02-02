package com.messaging.bootstrap.rcs.receiver.handler

import com.messaging.infrastructure.netty.protocol.ProtocolMessage
import com.messaging.usecase.rcs.RcsReceiveRequest
import com.messaging.usecase.rcs.RcsReceiveUseCase
import io.netty.channel.ChannelHandlerContext
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
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
            val response = ProtocolMessage.sendNack(
                msg.header.sequence,
                """{"code":"4000","message":"Invalid request format"}"""
            )
            ctx.writeAndFlush(response)
            return
        }

        runBlocking {
            val receiveRequest = RcsReceiveRequest(
                partnerId = partnerId,
                type = request.type,
                recipient = request.recipient,
                content = request.content,
                buttons = request.buttons,
                cards = request.cards
            )

            val result = rcsReceiveUseCase.receive(receiveRequest)

            if (result.success) {
                val response = ProtocolMessage.sendAck(
                    msg.header.sequence,
                    """{"messageId":"${result.messageId}"}"""
                )
                ctx.writeAndFlush(response)
            } else {
                val response = ProtocolMessage.sendNack(
                    msg.header.sequence,
                    """{"code":"${result.errorCode}","message":"${result.errorMessage}"}"""
                )
                ctx.writeAndFlush(response)
            }
        }
    }
}

data class SendRequestDto(
    val type: String,
    val recipient: String,
    val content: String? = null,
    val buttons: List<Map<String, Any?>> = emptyList(),
    val cards: List<Map<String, Any?>> = emptyList()
)
