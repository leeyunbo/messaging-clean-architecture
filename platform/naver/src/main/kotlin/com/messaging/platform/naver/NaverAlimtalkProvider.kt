package com.messaging.platform.naver

import com.messaging.core.naver.domain.NaverAlimtalkProvider
import com.messaging.core.naver.domain.NaverAlimtalkRequest
import com.messaging.core.naver.domain.NaverSendResult
import com.messaging.platform.naver.config.NaverApi
import com.messaging.platform.naver.dto.NaverAlimtalkMessageDto
import com.messaging.platform.naver.dto.NaverAlimtalkRequestDto
import com.messaging.platform.naver.dto.NaverButtonDto
import org.springframework.stereotype.Component

/**
 * 네이버 클라우드 알림톡 Provider
 */
@Component
class NaverAlimtalkProviderImpl(
    private val naverApiClient: NaverApiClient
) : NaverAlimtalkProvider {

    override suspend fun send(request: NaverAlimtalkRequest): NaverSendResult {
        if (!naverApiClient.isEnabled) {
            return NaverSendResult.fail("DISABLED", "Naver provider is disabled")
        }

        val buttons = request.buttons.map { btn ->
            NaverButtonDto(
                type = btn.type.name,
                name = btn.name,
                linkMobile = btn.linkMobile,
                linkPc = btn.linkPc
            )
        }.ifEmpty { null }

        val naverRequest = NaverAlimtalkRequestDto(
            templateCode = request.templateCode,
            plusFriendId = request.plusFriendId,
            messages = listOf(
                NaverAlimtalkMessageDto(
                    to = request.recipient,
                    content = request.content,
                    buttons = buttons
                )
            )
        )

        val path = NaverApi.ALIMTALK_SEND_PATH_TEMPLATE.format(naverApiClient.serviceId)
        return naverApiClient.send(
            path = path,
            request = naverRequest,
            messageId = request.messageId,
            messageType = "Alimtalk"
        )
    }
}
