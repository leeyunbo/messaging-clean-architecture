package com.messaging.bootstrap.rcs

import com.messaging.library.webhook.WebhookSignatureVerifier
import com.messaging.platform.rcs.webhook.RcsWebhookParser
import com.messaging.platform.rcs.webhook.RcsWebhookPayload
import com.messaging.usecase.rcs.RcsReportUseCase
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tools.jackson.databind.ObjectMapper

@RestController
@RequestMapping("/webhook/rcs")
@EnableConfigurationProperties(RcsWebhookProperties::class)
class RcsWebhookController(
    private val rcsReportUseCase: RcsReportUseCase,
    private val properties: RcsWebhookProperties,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping
    fun receive(
        @RequestBody rawBody: String,
        @RequestHeader("X-Signature", required = false) signature: String?
    ): ResponseEntity<Unit> {
        if (!verifySignature(rawBody, signature)) {
            log.warn("Invalid signature for RCS webhook")
            return ResponseEntity.status(401).build()
        }

        val payload: RcsWebhookPayload = try {
            objectMapper.readValue(rawBody, RcsWebhookPayload::class.java)
        } catch (e: Exception) {
            log.error("Failed to parse webhook payload: {}", e.message)
            return ResponseEntity.badRequest().build()
        }

        log.info("Received RCS webhook: messageId={}, status={}", payload.messageId, payload.status)

        runBlocking {
            try {
                val report = RcsWebhookParser.toReport(payload)
                rcsReportUseCase.publish(report)
                log.info("Report published: messageId={}, code={}", report.messageId, report.code)
            } catch (e: Exception) {
                log.error("Failed to process webhook: messageId={}, error={}", payload.messageId, e.message, e)
            }
        }

        return ResponseEntity.ok().build()
    }

    private fun verifySignature(payload: String, signature: String?): Boolean {
        if (signature.isNullOrBlank()) return false

        return WebhookSignatureVerifier.verifyHmacSha256(
            payload = payload,
            signature = signature,
            secretKey = properties.secretKey
        )
    }
}
