package com.messaging.library.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 구조화된 로거 래퍼
 * key=value 형태의 구조화된 로깅 지원
 */
class StructuredLogger(private val delegate: Logger) {

    fun info(message: String, vararg pairs: Pair<String, Any?>) {
        if (delegate.isInfoEnabled) {
            delegate.info(formatMessage(message, pairs))
        }
    }

    fun warn(message: String, vararg pairs: Pair<String, Any?>) {
        if (delegate.isWarnEnabled) {
            delegate.warn(formatMessage(message, pairs))
        }
    }

    fun error(message: String, vararg pairs: Pair<String, Any?>) {
        if (delegate.isErrorEnabled) {
            delegate.error(formatMessage(message, pairs))
        }
    }

    fun error(message: String, throwable: Throwable, vararg pairs: Pair<String, Any?>) {
        if (delegate.isErrorEnabled) {
            delegate.error(formatMessage(message, pairs), throwable)
        }
    }

    fun debug(message: String, vararg pairs: Pair<String, Any?>) {
        if (delegate.isDebugEnabled) {
            delegate.debug(formatMessage(message, pairs))
        }
    }

    private fun formatMessage(message: String, pairs: Array<out Pair<String, Any?>>): String {
        if (pairs.isEmpty()) return message
        val kvString = pairs.joinToString(", ") { (k, v) -> "$k=$v" }
        return "$message | $kvString"
    }

    companion object {
        fun getLogger(clazz: Class<*>): StructuredLogger {
            return StructuredLogger(LoggerFactory.getLogger(clazz))
        }

        inline fun <reified T> getLogger(): StructuredLogger {
            return getLogger(T::class.java)
        }
    }
}
