package com.messaging.infrastructure.netty.protocol

data class MessageHeader(
    val type: MessageType,
    val length: Int,
    val sequence: Long
) {
    companion object {
        const val HEADER_SIZE = 20
        const val STX: Byte = 0x02
        const val ETX: Byte = 0x03
    }
}
