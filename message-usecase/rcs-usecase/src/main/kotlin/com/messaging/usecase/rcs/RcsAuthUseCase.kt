package com.messaging.usecase.rcs

import com.messaging.core.partner.domain.Partner
import com.messaging.core.partner.domain.PartnerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RcsAuthUseCase(
    private val partnerRepository: PartnerRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun authenticate(partnerId: String, secretKey: String): AuthResult {
        log.info("Authenticating partner: partnerId={}", partnerId)

        val partner = partnerRepository.findByPartnerId(partnerId)
            ?: return AuthResult.failure("4011", "Partner not found")

        if (!partner.active) {
            log.warn("Partner is inactive: partnerId={}", partnerId)
            return AuthResult.failure("4012", "Partner is inactive")
        }

        if (partner.apiSecret != secretKey) {
            log.warn("Invalid secret key: partnerId={}", partnerId)
            return AuthResult.failure("4013", "Invalid credentials")
        }

        log.info("Authentication successful: partnerId={}", partnerId)
        return AuthResult.success(partner)
    }
}

data class AuthResult(
    val success: Boolean,
    val partner: Partner? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null
) {
    companion object {
        fun success(partner: Partner) = AuthResult(success = true, partner = partner)
        fun failure(code: String, message: String) = AuthResult(
            success = false,
            errorCode = code,
            errorMessage = message
        )
    }
}
