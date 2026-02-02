package com.messaging.infrastructure.netty.protocol

import io.netty.buffer.ByteBuf

data class MessageHeader(
    val type: MessageType,
    val length: Int,
    val sequence: Long
) {
    /**
     * 헤더를 ByteBuf에 직렬화
     */
    fun writeTo(buf: ByteBuf, bodyLength: Int) {
        buf.writeByte(STX.toInt())
        buf.writeBytes(type.code.toByteArray(Charsets.UTF_8))
        buf.writeInt(bodyLength)
        buf.writeLong(sequence)
        buf.writeZero(RESERVED_SIZE)
        buf.writeByte(ETX.toInt())
    }

    companion object {
        // 프로토콜 상수
        const val STX: Byte = 0x02
        const val ETX: Byte = 0x03

        // 필드 크기 상수
        const val STX_SIZE = 1
        const val TYPE_SIZE = 2
        const val LENGTH_SIZE = 4
        const val SEQ_SIZE = 8
        const val RESERVED_SIZE = 4
        const val ETX_SIZE = 1

        const val HEADER_SIZE = STX_SIZE + TYPE_SIZE + LENGTH_SIZE + SEQ_SIZE + RESERVED_SIZE + ETX_SIZE

        /**
         * ByteBuf에서 헤더 역직렬화
         * @return MessageHeader 또는 null (유효하지 않은 경우)
         */
        fun readFrom(buf: ByteBuf): ReadResult {
            if (buf.readableBytes() < HEADER_SIZE) {
                return ReadResult.InsufficientData
            }

            buf.markReaderIndex()

            // STX 확인
            val stx = buf.readByte()
            if (stx != STX) {
                buf.resetReaderIndex()
                buf.skipBytes(1)
                return ReadResult.InvalidStx(stx)
            }

            // TYPE
            val typeBytes = ByteArray(TYPE_SIZE)
            buf.readBytes(typeBytes)
            val typeCode = String(typeBytes, Charsets.UTF_8)
            val type = MessageType.fromCode(typeCode)
            if (type == null) {
                buf.resetReaderIndex()
                buf.skipBytes(1)
                return ReadResult.InvalidType(typeCode)
            }

            // LENGTH
            val length = buf.readInt()

            // SEQ
            val sequence = buf.readLong()

            // RESERVED (skip)
            buf.skipBytes(RESERVED_SIZE)

            // ETX 확인
            val etx = buf.readByte()
            if (etx != ETX) {
                buf.resetReaderIndex()
                buf.skipBytes(1)
                return ReadResult.InvalidEtx(etx)
            }

            return ReadResult.Success(MessageHeader(type, length, sequence))
        }
    }

    sealed class ReadResult {
        data class Success(val header: MessageHeader) : ReadResult()
        data object InsufficientData : ReadResult()
        data class InvalidStx(val actual: Byte) : ReadResult()
        data class InvalidEtx(val actual: Byte) : ReadResult()
        data class InvalidType(val typeCode: String) : ReadResult()
    }
}
