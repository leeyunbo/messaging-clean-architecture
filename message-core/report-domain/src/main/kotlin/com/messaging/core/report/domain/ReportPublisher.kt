package com.messaging.core.report.domain

/**
 * 리포트 발행 Port
 * Infrastructure에서 구현 (RabbitMQ 등)
 */
interface ReportPublisher {
    suspend fun publish(report: Report)
}
