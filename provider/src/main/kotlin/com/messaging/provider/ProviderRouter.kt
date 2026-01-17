package com.messaging.provider

import com.messaging.common.domain.Carrier
import com.messaging.common.domain.MessageType
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * MessageType + Carrier 조합에 따라 적절한 Provider를 선택하여 발송
 */
@Component
class ProviderRouter(
    private val providers: List<MessageProvider>
) {
    private data class ProviderKey(val type: MessageType, val carrier: Carrier?)

    private val providerMap: Map<ProviderKey, MessageProvider> by lazy {
        providers.flatMap { provider ->
            provider.supportedTypes().map { type ->
                ProviderKey(type, provider.supportedCarrier()) to provider
            }
        }.toMap()
    }

    fun send(request: SendRequest): Mono<SendResult> {
        val provider = getProvider(request.messageType, request.carrier)
            ?: return Mono.just(
                SendResult.fail(
                    resultCode = "P001",
                    resultMessage = "지원하지 않는 메시지 타입/통신사: ${request.messageType}/${request.carrier}"
                )
            )

        return provider.send(request)
    }

    fun getProvider(type: MessageType, carrier: Carrier?): MessageProvider? =
        providerMap[ProviderKey(type, carrier)]
}
