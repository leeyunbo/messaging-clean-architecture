package com.messaging.platform.skt

import com.messaging.core.sms.domain.*
import com.messaging.platform.skt.config.SktApi
import com.messaging.platform.skt.dto.SktLmsRequest
import org.springframework.stereotype.Component

/**
 * SKT LMS/MMS 발송 Provider
 */
@Component
class SktLmsProvider(
    private val sktApiClient: SktApiClient
) : SmsProvider {

    override fun supportedTypes(): Set<SmsType> = setOf(SmsType.LMS, SmsType.MMS)

    override fun supportedCarrier(): Carrier = Carrier.SKT

    override suspend fun send(request: SmsSendRequest): SmsSendResult {
        if (!sktApiClient.isEnabled) {
            return SmsSendResult.fail("DISABLED", "SKT provider is disabled")
        }

        val msgType = if (request.type == SmsType.MMS) "MMS" else "LMS"
        val sktRequest = SktLmsRequest(
            msgId = request.messageId,
            phone = request.recipient,
            callback = request.callback,
            subject = request.subject,
            msg = request.content,
            msgType = msgType,
            filePath = request.imageUrl
        )

        return sktApiClient.send(
            path = SktApi.MMS_SEND_PATH,
            request = sktRequest,
            messageId = request.messageId,
            messageType = msgType
        )
    }
}
