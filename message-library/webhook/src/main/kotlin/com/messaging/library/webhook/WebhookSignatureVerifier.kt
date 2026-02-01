package com.messaging.library.webhook

import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * 웹훅 서명 검증 유틸리티
 */
object WebhookSignatureVerifier {

    /**
     * HMAC-SHA256 서명 검증
     */
    fun verifyHmacSha256(
        payload: String,
        signature: String,
        secretKey: String
    ): Boolean {
        val expectedSignature = generateHmacSha256(payload, secretKey)
        return signature == expectedSignature
    }

    /**
     * HMAC-SHA256 서명 생성
     */
    fun generateHmacSha256(payload: String, secretKey: String): String {
        val algorithm = "HmacSHA256"
        val signingKey = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), algorithm)
        val mac = Mac.getInstance(algorithm)
        mac.init(signingKey)
        val rawHmac = mac.doFinal(payload.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(rawHmac)
    }

    /**
     * 타임스탬프 기반 서명 검증
     */
    fun verifyWithTimestamp(
        payload: String,
        signature: String,
        timestamp: Long,
        secretKey: String,
        maxAgeMillis: Long = 300_000 // 기본 5분
    ): Boolean {
        val now = System.currentTimeMillis()
        if (now - timestamp > maxAgeMillis) {
            return false
        }

        val message = "$timestamp.$payload"
        return verifyHmacSha256(message, signature, secretKey)
    }
}
