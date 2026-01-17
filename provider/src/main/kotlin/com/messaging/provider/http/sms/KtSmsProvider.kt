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
class KtSmsProvider(
    webClient: WebClient,
    circuitBreaker: CircuitBreaker,
    @param:Value("\${provider.kt.sms.endpoint:/api/kt/sms}")
    private val endpointPath: String = "/api/kt/sms"
) : AbstractHttpProvider<KtSmsProvider.Response>(webClient, circuitBreaker, Response::class.java) {

    override fun supportedTypes(): Set<MessageType> = setOf(MessageType.SMS)

    override fun supportedCarrier(): Carrier = Carrier.KT

    override val logPrefix: String = "KT-SMS"

    override val endpoint: String = endpointPath

    override fun buildVendorRequest(request: SendRequest): VendorRequest {
        val body = buildMap {
            put("msgId", request.messageId)
            putAll(request.detail)
        }
        return VendorRequest(body = body)
    }

    override fun mapToSendResult(response: Response): SendResult {
        return if (response.status == 0) {
            SendResult.success(response.message ?: "발송 성공")
        } else {
            SendResult.fail(
                resultCode = "KT_${response.status}",
                resultMessage = response.message ?: "발송 실패"
            )
        }
    }

    data class Response(
        val status: Int = -1,
        val message: String? = null
    )
}
