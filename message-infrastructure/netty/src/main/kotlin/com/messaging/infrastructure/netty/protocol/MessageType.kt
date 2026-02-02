package com.messaging.infrastructure.netty.protocol

enum class MessageType(val code: String) {
    AUTH("AU"),
    AUTH_ACK("AA"),
    AUTH_NACK("AN"),
    SEND("SR"),
    SEND_ACK("SA"),
    SEND_NACK("SN");

    companion object {
        private val codeMap = entries.associateBy { it.code }

        fun fromCode(code: String): MessageType? = codeMap[code]
    }
}
