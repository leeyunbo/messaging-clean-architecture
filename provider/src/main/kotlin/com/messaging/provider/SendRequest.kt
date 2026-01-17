package com.messaging.provider

import com.messaging.common.domain.Carrier
import com.messaging.common.domain.MessageType

/**
 * Provider 발송 요청
 */
data class SendRequest(
    val messageId: String,
    val messageType: MessageType,
    val carrier: Carrier? = null,
    val recipient: String,
    val content: String,
    val detail: Map<String, Any?> = emptyMap()
) {
    @Suppress("UNCHECKED_CAST")
    fun <T> getDetail(key: String): T? = detail[key] as? T
}
