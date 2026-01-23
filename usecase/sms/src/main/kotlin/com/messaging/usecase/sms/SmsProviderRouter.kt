package com.messaging.usecase.sms

import com.messaging.core.sms.domain.Carrier
import com.messaging.core.sms.domain.SmsSendRequest
import com.messaging.core.sms.domain.SmsProvider
import com.messaging.core.sms.domain.SmsType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * SMS Provider 라우터
 * 요청에 적합한 Provider 선택
 */
@Component
class SmsProviderRouter(
    private val providers: List<SmsProvider>
) {
    private val log = LoggerFactory.getLogger(javaClass)

    // 통신사별 Provider 캐시
    private val carrierProviders: Map<Carrier, List<SmsProvider>> by lazy {
        providers
            .filter { it.supportedTypes().contains(SmsType.SMS) }
            .filter { it.supportedCarrier() != null }
            .groupBy { it.supportedCarrier()!! }
    }

    // 통신사 무관 Provider (fallback용)
    private val genericProviders: List<SmsProvider> by lazy {
        providers.filter { it.supportedTypes().contains(SmsType.SMS) }
    }

    fun route(request: SmsSendRequest): SmsProvider? {
        // 1. 요청에 통신사가 지정된 경우 해당 통신사 Provider 사용
        if (request.carrier != null) {
            val carrierProvider = carrierProviders[request.carrier]?.firstOrNull()
            if (carrierProvider != null) {
                log.debug("Selected carrier-specific provider: carrier={}, provider={}",
                    request.carrier, carrierProvider.javaClass.simpleName)
                return carrierProvider
            }
        }

        // 2. 타입 지원하는 Provider 선택 (fallback)
        val typeProvider = genericProviders.firstOrNull { it.supportedTypes().contains(request.type) }
        if (typeProvider != null) {
            log.debug("Selected type-matching provider: provider={}", typeProvider.javaClass.simpleName)
            return typeProvider
        }

        log.error("No SMS provider available for type={}", request.type)
        return null
    }
}
