package com.messaging.provider.http.naver

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
class NaverTalkProvider(
    webClient: WebClient,
    circuitBreaker: CircuitBreaker,
    @param:Value("\${provider.naver.talk.endpoint:/api/naver/talk}")
    private val endpointPath: String = "/api/naver/talk"
) : AbstractHttpProvider<NaverTalkProvider.Response>(webClient, circuitBreaker, Response::class.java) {

    override fun supportedTypes(): Set<MessageType> = setOf(MessageType.NAVER_TALK)

    override fun supportedCarrier(): Carrier? = null

    override val logPrefix: String = "NAVER-TALK"

    override val endpoint: String = endpointPath

    override fun buildVendorRequest(request: SendRequest): VendorRequest {
        val body = buildMap {
            put("serialNumber", request.messageId)
            putAll(request.detail)
        }
        return VendorRequest(body = body)
    }

    override fun mapToSendResult(response: Response): SendResult {
        return if (response.success) {
            SendResult.success(response.message ?: "발송 성공")
        } else {
            SendResult.fail(
                resultCode = response.errorCode ?: "NAVER_ERROR",
                resultMessage = response.message ?: "발송 실패"
            )
        }
    }

    data class Response(
        val success: Boolean = false,
        val errorCode: String? = null,
        val message: String? = null
    )
}
