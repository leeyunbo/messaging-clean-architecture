package com.messaging.platform.skt

import com.messaging.core.sms.domain.*
import com.messaging.platform.skt.config.SktApi
import com.messaging.platform.skt.dto.SktSmsRequest
import org.springframework.stereotype.Component

/**
 * SKT SMS Provider
 */
@Component
class SktSmsProvider(
    private val sktApiClient: SktApiClient
) : SmsProvider {

    override fun supportedTypes(): Set<SmsType> = setOf(SmsType.SMS, SmsType.LMS, SmsType.MMS)

    override fun supportedCarrier(): Carrier = Carrier.SKT

    override suspend fun send(request: SmsSendRequest): SmsSendResult {
        if (!sktApiClient.isEnabled) {
            return SmsSendResult.fail("DISABLED", "SKT provider is disabled")
        }

        val sktRequest = SktSmsRequest(
            msgId = request.messageId,
            phone = request.recipient,
            callback = request.callback,
            msg = request.content,
            msgType = request.type.name
        )

        return sktApiClient.send(
            path = SktApi.SMS_SEND_PATH,
            request = sktRequest,
            messageId = request.messageId,
            messageType = "SMS"
        )
    }
}
