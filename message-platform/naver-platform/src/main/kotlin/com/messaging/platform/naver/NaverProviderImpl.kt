package com.messaging.platform.naver

import com.messaging.core.naver.domain.NaverProvider
import com.messaging.core.naver.domain.NaverSendRequest
import com.messaging.core.naver.domain.NaverSendResult
import com.messaging.platform.naver.config.NaverApi
import com.messaging.platform.naver.config.NaverProperties
import org.springframework.stereotype.Component

@Component
class NaverProviderImpl(
    private val apiClient: NaverApiClient,
    private val config: NaverProperties
) : NaverProvider {

    override suspend fun send(request: NaverSendRequest): NaverSendResult {
        if (!config.enabled) {
            return NaverSendResult.fail("DISABLED", "Naver provider is disabled")
        }

        val body = request.toApiBody()
        val path = NaverApi.SEND_PATH_TEMPLATE.format(config.serviceId)
        return apiClient.send(path, body, request.messageId)
    }

    private fun NaverSendRequest.toApiBody() = mapOf(
        "to" to recipient,
        "content" to content
    )
}
