package com.messaging.platform.kakaodirect

import com.messaging.core.kakao.domain.BrandMessageProvider
import com.messaging.core.kakao.domain.BrandMessageRequest
import com.messaging.core.kakao.domain.KakaoSendResult
import com.messaging.library.idgen.MessageIdGenerator
import org.springframework.stereotype.Component

/**
 * 카카오 다이렉트 브랜드메시지 Provider
 */
@Component
class KakaoDirectBrandMessageProvider(
    private val apiClient: KakaoDirectApiClient
) : BrandMessageProvider {

    override suspend fun send(request: BrandMessageRequest): KakaoSendResult {
        if (!apiClient.isEnabled) {
            return KakaoSendResult.fail("DISABLED", "Kakao Direct provider is disabled")
        }

        val serialNumber = MessageIdGenerator.generate()
        val path = request.brandMessageType.path

        // variables를 그대로 body로 전송, serialNumber는 헤더로
        return apiClient.send(
            path = path,
            body = request.variables,
            messageId = request.messageId,
            serialNumber = serialNumber
        )
    }
}
