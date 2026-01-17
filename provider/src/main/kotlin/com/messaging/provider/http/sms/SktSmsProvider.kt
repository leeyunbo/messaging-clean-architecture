package com.messaging.provider.http.sms

import com.messaging.common.domain.Carrier
import com.messaging.common.domain.MessageType
import com.messaging.provider.SendRequest
import com.messaging.provider.SendResult
import com.messaging.provider.VendorRequest
import com.messaging.provider.http.AbstractHttpProvider
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class SktSmsProvider(
    webClient: WebClient,
    circuitBreaker: CircuitBreaker,
    @param:Value("\${provider.skt.sms.endpoint:/api/skt/sms}")
    private val endpointPath: String = "/api/skt/sms"
) : AbstractHttpProvider<SktSmsProvider.Response>(webClient, circuitBreaker, Response::class.java) {

    override fun supportedTypes(): Set<MessageType> = setOf(MessageType.SMS)

    override fun supportedCarrier(): Carrier = Carrier.SKT

    override val logPrefix: String = "SKT-SMS"

    override val endpoint: String = endpointPath

    override fun buildVendorRequest(request: SendRequest): VendorRequest {
        val body = buildMap {
            put("msgId", request.messageId)
            putAll(request.detail)
        }
        return VendorRequest(body = body)
    }

    override fun mapToSendResult(response: Response): SendResult {
        return if (response.resultCode == "0000") {
            SendResult.success(response.resultMsg ?: "발송 성공")
        } else {
            SendResult.fail(
                resultCode = response.resultCode,
                resultMessage = response.resultMsg ?: "발송 실패"
            )
        }
    }

    data class Response(
        val resultCode: String = "",
        val resultMsg: String? = null
    )
}
