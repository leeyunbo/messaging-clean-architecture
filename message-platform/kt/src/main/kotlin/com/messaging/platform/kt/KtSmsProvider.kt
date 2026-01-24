package com.messaging.platform.kt

import com.messaging.core.sms.domain.*
import com.messaging.platform.kt.config.KtApi
import com.messaging.platform.kt.dto.KtSmsRequest
import org.springframework.stereotype.Component

/**
 * KT SMS Provider
 */
@Component
class KtSmsProvider(
    private val ktApiClient: KtApiClient
) : SmsProvider {

    override fun supportedTypes(): Set<SmsType> = setOf(SmsType.SMS, SmsType.LMS, SmsType.MMS)

    override fun supportedCarrier(): Carrier = Carrier.KT

    override suspend fun send(request: SmsSendRequest): SmsSendResult {
        if (!ktApiClient.isEnabled) {
            return SmsSendResult.fail("DISABLED", "KT provider is disabled")
        }

        val ktRequest = KtSmsRequest(
            msgId = request.messageId,
            receiver = request.recipient,
            sender = request.callback,
            message = request.content
        )

        return ktApiClient.send(
            path = KtApi.SMS_SEND_PATH,
            request = ktRequest,
            messageId = request.messageId,
            messageType = "SMS"
        )
    }
}
