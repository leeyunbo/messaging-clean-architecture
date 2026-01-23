package com.messaging.infrastructure.rabbitmq

import com.messaging.core.report.domain.Report
import com.messaging.core.report.domain.ReportPublisher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * RabbitMQ 기반 리포트 발행자 (Spring AMQP)
 * Blocking 호출을 Dispatchers.IO로 오프로딩
 */
@Component
class RabbitMqReportPublisher(
    private val rabbitTemplate: RabbitTemplate,
    @param:Value("\${rabbitmq.report.exchange:report-exchange}") private val exchange: String,
    @param:Value("\${rabbitmq.report.routing-key:report}") private val routingKey: String
) : ReportPublisher {
    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun publish(report: Report) {
        withContext(Dispatchers.IO) {
            rabbitTemplate.convertAndSend(exchange, routingKey, report)
        }
        log.debug("Report published to RabbitMQ: messageId={}", report.messageId)
    }
}
