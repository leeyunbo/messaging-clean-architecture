package com.messaging.core.rcs.domain

/**
 * 전화번호 Value Object
 * 한국 휴대폰 번호 형식 검증 (01X-XXXX-XXXX)
 */
@JvmInline
value class PhoneNumber private constructor(val value: String) {

    companion object {
        private val PATTERN = Regex("^01[0-9]{8,9}$")

        fun of(value: String): PhoneNumber {
            require(PATTERN.matches(value)) {
                "Invalid phone number format: $value"
            }
            return PhoneNumber(value)
        }

        fun ofOrNull(value: String): PhoneNumber? {
            return if (PATTERN.matches(value)) PhoneNumber(value) else null
        }
    }
}
