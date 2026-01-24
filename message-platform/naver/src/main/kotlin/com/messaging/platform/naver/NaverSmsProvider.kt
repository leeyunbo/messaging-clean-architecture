package com.messaging.platform.naver

import com.messaging.core.naver.domain.NaverSendResult
import com.messaging.core.naver.domain.NaverSmsProvider
import com.messaging.core.naver.domain.NaverSmsRequest
import com.messaging.core.naver.domain.NaverSmsType
import com.messaging.platform.naver.config.NaverApi
import com.messaging.platform.naver.dto.NaverSmsMessageDto
import com.messaging.platform.naver.dto.NaverSmsRequestDto
import org.springframework.stereotype.Component

/**
 * 네이버 클라우드 SMS Provider
 */
@Component
class NaverSmsProviderImpl(
    private val naverApiClient: NaverApiClient
) : NaverSmsProvider {

    override suspend fun send(request: NaverSmsRequest): NaverSendResult {
        if (!naverApiClient.isEnabled) {
            return NaverSendResult.fail("DISABLED", "Naver provider is disabled")
        }

        val msgType = when (request.type) {
            NaverSmsType.MMS -> "MMS"
            NaverSmsType.LMS -> "LMS"
            else -> "SMS"
        }

        val naverRequest = NaverSmsRequestDto(
            type = msgType,
            from = request.callback,
            content = request.content,
            messages = listOf(
                NaverSmsMessageDto(
                    to = request.recipient,
                    subject = request.subject
                )
            )
        )

        val path = NaverApi.SMS_SEND_PATH_TEMPLATE.format(naverApiClient.serviceId)
        return naverApiClient.send(
            path = path,
            request = naverRequest,
            messageId = request.messageId,
            messageType = "SMS"
        )
    }
}
