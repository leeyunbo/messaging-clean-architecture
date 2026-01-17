package com.messaging.provider.http.kakao

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
class KakaoBrandMessageProvider(
    webClient: WebClient,
    circuitBreaker: CircuitBreaker,
    @param:Value("\${provider.kakao.brand-message.endpoint:/api/kakao/brand-message}")
    private val endpointPath: String = "/api/kakao/brand-message"
) : AbstractHttpProvider<KakaoBrandMessageProvider.Response>(webClient, circuitBreaker, Response::class.java) {

    override fun supportedTypes(): Set<MessageType> = setOf(MessageType.KAKAO_BRAND_MESSAGE)

    override fun supportedCarrier(): Carrier? = null

    override val logPrefix: String = "KAKAO-BRAND-MESSAGE"

    override val endpoint: String = endpointPath

    override fun buildVendorRequest(request: SendRequest): VendorRequest {
        return VendorRequest(
            headers = mapOf("X-Serial-Number" to request.messageId),
            body = request.detail
        )
    }

    override fun mapToSendResult(response: Response): SendResult {
        return if (response.resultCode == 0) {
            SendResult.success(response.resultMessage ?: "발송 성공")
        } else {
            SendResult.fail(
                resultCode = "KAKAO_${response.resultCode}",
                resultMessage = response.resultMessage ?: "발송 실패"
            )
        }
    }

    data class Response(
        val resultCode: Int = -1,
        val resultMessage: String? = null
    )
}
