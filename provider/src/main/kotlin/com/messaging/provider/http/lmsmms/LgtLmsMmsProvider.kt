package com.messaging.provider.http.lmsmms

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
class LgtLmsMmsProvider(
    webClient: WebClient,
    circuitBreaker: CircuitBreaker,
    @param:Value("\${provider.lgt.lmsmms.endpoint:/api/lgt/lmsmms}")
    private val endpointPath: String = "/api/lgt/lmsmms"
) : AbstractHttpProvider<LgtLmsMmsProvider.Response>(webClient, circuitBreaker, Response::class.java) {

    override fun supportedTypes(): Set<MessageType> = setOf(MessageType.LMS, MessageType.MMS)

    override fun supportedCarrier(): Carrier = Carrier.LGT

    override val logPrefix: String = "LGT-LMS/MMS"

    override val endpoint: String = endpointPath

    override fun buildVendorRequest(request: SendRequest): VendorRequest {
        val body = buildMap {
            put("msgId", request.messageId)
            putAll(request.detail)
        }
        return VendorRequest(body = body)
    }

    override fun mapToSendResult(response: Response): SendResult {
        return if (response.code == "SUCCESS") {
            SendResult.success(response.description ?: "발송 성공")
        } else {
            SendResult.fail(
                resultCode = response.code,
                resultMessage = response.description ?: "발송 실패"
            )
        }
    }

    data class Response(
        val code: String = "",
        val description: String? = null
    )
}
