package com.messaging.usecase.lmsmms

import com.messaging.core.sms.domain.Carrier
import com.messaging.core.sms.domain.SmsSendRequest
import com.messaging.core.sms.domain.SmsProvider
import com.messaging.core.sms.domain.SmsType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * LMS/MMS Provider 라우터
 */
@Component
class LmsMmsProviderRouter(
    private val providers: List<SmsProvider>
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val supportedTypes = setOf(SmsType.LMS, SmsType.MMS)

    private val carrierProviders: Map<Carrier, List<SmsProvider>> by lazy {
        providers
            .filter { it.supportedTypes().any { type -> type in supportedTypes } }
            .filter { it.supportedCarrier() != null }
            .groupBy { it.supportedCarrier()!! }
    }

    fun route(request: SmsSendRequest): SmsProvider? {
        // 통신사 지정된 경우
        if (request.carrier != null) {
            val carrierProvider = carrierProviders[request.carrier]?.firstOrNull()
            if (carrierProvider != null) {
                log.debug("Selected carrier-specific provider: carrier={}", request.carrier)
                return carrierProvider
            }
        }

        // 타입 지원하는 아무 Provider
        val anyProvider = carrierProviders.values.flatten()
            .firstOrNull { it.supportedTypes().contains(request.type) }
        if (anyProvider != null) {
            log.debug("Selected fallback provider")
            return anyProvider
        }

        log.error("No LMS/MMS provider available")
        return null
    }
}
