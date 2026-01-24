package com.messaging.bootstrap.reporter.consumer

import com.messaging.core.partner.domain.PartnerRepository
import com.messaging.infrastructure.rabbitmq.config.QueueConstants
import com.messaging.infrastructure.rabbitmq.message.ResultEnvelope
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

/**
 * 결과 소비자 (Spring AMQP)
 * 발송 결과를 수신하여 파트너 웹훅으로 전달
 */
@Component
class ResultConsumer(
    private val partnerRepository: PartnerRepository,
    private val webClient: WebClient
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val webhookRetry = Retry.of("webhook", RetryConfig.custom<Any>()
        .maxAttempts(3)
        .waitDuration(Duration.ofSeconds(1))
        .build())

    @RabbitListener(queues = [QueueConstants.RESULT_QUEUE])
    fun onMessage(envelope: ResultEnvelope) {
        log.info("Received result: messageId={}, status={}", envelope.messageId, envelope.status)

        runBlocking {
            try {
                sendWebhook(envelope)
                log.info("Result processed: messageId={}", envelope.messageId)
            } catch (e: Exception) {
                log.error("Failed to process result: messageId={}, error={}", envelope.messageId, e.message, e)
                throw e
            }
        }
    }

    private suspend fun sendWebhook(envelope: ResultEnvelope) {
        val partner = partnerRepository.findByPartnerId(envelope.partnerId)
        if (partner == null) {
            log.warn("Partner not found: partnerId={}", envelope.partnerId)
            return
        }

        if (partner.webhookUrl.isBlank()) {
            log.debug("No webhook URL configured: partnerId={}", envelope.partnerId)
            return
        }

        val webhookPayload = mapOf(
            "messageId" to envelope.messageId,
            "clientMsgId" to envelope.clientMsgId,
            "status" to envelope.status,
            "resultCode" to envelope.resultCode,
            "resultMessage" to envelope.resultMessage,
            "sentAt" to envelope.sentAt?.toString(),
            "processedAt" to envelope.processedAt.toString()
        )

        try {
            webhookRetry.executeCallable {
                webClient.post()
                    .uri(partner.webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Webhook-Secret", partner.webhookSecret)
                    .bodyValue(webhookPayload)
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(10))
                    .block()
            }
            log.info("Webhook sent: messageId={}, partnerId={}", envelope.messageId, envelope.partnerId)
        } catch (e: Exception) {
            log.error("Failed to send webhook after retries: messageId={}, error={}", envelope.messageId, e.message)
        }
    }
}
