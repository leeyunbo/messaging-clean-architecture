package com.messaging.infrastructure.netty.protocol

data class ProtocolMessage(
    val header: MessageHeader,
    val body: String
) {
    companion object {
        fun auth(sequence: Long, body: String) = ProtocolMessage(
            header = MessageHeader(MessageType.AUTH, body.toByteArray(Charsets.UTF_8).size, sequence),
            body = body
        )

        fun authAck(sequence: Long) = ProtocolMessage(
            header = MessageHeader(MessageType.AUTH_ACK, 0, sequence),
            body = ""
        )

        fun authNack(sequence: Long, body: String) = ProtocolMessage(
            header = MessageHeader(MessageType.AUTH_NACK, body.toByteArray(Charsets.UTF_8).size, sequence),
            body = body
        )

        fun send(sequence: Long, body: String) = ProtocolMessage(
            header = MessageHeader(MessageType.SEND, body.toByteArray(Charsets.UTF_8).size, sequence),
            body = body
        )

        fun sendAck(sequence: Long, body: String) = ProtocolMessage(
            header = MessageHeader(MessageType.SEND_ACK, body.toByteArray(Charsets.UTF_8).size, sequence),
            body = body
        )

        fun sendNack(sequence: Long, body: String) = ProtocolMessage(
            header = MessageHeader(MessageType.SEND_NACK, body.toByteArray(Charsets.UTF_8).size, sequence),
            body = body
        )
    }
}
