package com.messaging.platform.lgt

import com.messaging.core.sms.domain.*
import com.messaging.platform.lgt.config.LgtApi
import com.messaging.platform.lgt.dto.LgtRequest
import org.springframework.stereotype.Component

/**
 * LG U+ SMS Provider
 */
@Component
class LgtSmsProvider(
    private val lgtApiClient: LgtApiClient
) : SmsProvider {

    override fun supportedTypes(): Set<SmsType> = setOf(SmsType.SMS, SmsType.LMS, SmsType.MMS)

    override fun supportedCarrier(): Carrier = Carrier.LGT

    override suspend fun send(request: SmsSendRequest): SmsSendResult {
        if (!lgtApiClient.isEnabled) {
            return SmsSendResult.fail("DISABLED", "LGT provider is disabled")
        }

        val lgtRequest = LgtRequest(
            transactionId = request.messageId,
            to = request.recipient,
            from = request.callback,
            text = request.content,
            type = request.type.name
        )

        return lgtApiClient.send(
            path = LgtApi.MESSAGE_SEND_PATH,
            request = lgtRequest,
            messageId = request.messageId,
            messageType = "SMS"
        )
    }
}
