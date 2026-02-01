package com.messaging.platform.rcs

import com.messaging.core.rcs.domain.*
import com.messaging.platform.rcs.config.RcsProperties
import org.springframework.stereotype.Component

@Component
class RcsProviderImpl(
    private val apiClient: RcsApiClient,
    private val config: RcsProperties
) : RcsProvider {

    override suspend fun send(request: RcsSendRequest): RcsSendResult {
        if (!config.enabled) {
            return RcsSendResult.fail("DISABLED", "RCS provider is disabled")
        }

        return when (request) {
            is RcsStandaloneRequest -> sendStandalone(request)
            is RcsCarouselRequest -> sendCarousel(request)
        }
    }

    private suspend fun sendStandalone(request: RcsStandaloneRequest): RcsSendResult {
        val body = buildMap {
            put("recipient", request.recipient)
            put("content", request.content)
            if (request.buttons.isNotEmpty()) {
                put("buttons", request.buttons.map { it.toApiButton() })
            }
        }
        return apiClient.sendStandalone(body, request.messageId)
    }

    private suspend fun sendCarousel(request: RcsCarouselRequest): RcsSendResult {
        val body = mapOf(
            "recipient" to request.recipient,
            "cards" to request.cards.map { it.toApiCard() }
        )
        return apiClient.sendCarousel(body, request.messageId)
    }

    private fun RcsButton.toApiButton() = mapOf(
        "type" to type.name,
        "text" to text,
        "url" to url,
        "phoneNumber" to phoneNumber,
        "payload" to payload
    ).filterValues { it != null }

    private fun RcsCard.toApiCard() = buildMap {
        put("title", title)
        description?.let { put("description", it) }
        mediaUrl?.let { put("mediaUrl", it) }
        put("mediaType", mediaType.name)
        if (buttons.isNotEmpty()) {
            put("buttons", buttons.map { it.toApiButton() })
        }
    }
}
