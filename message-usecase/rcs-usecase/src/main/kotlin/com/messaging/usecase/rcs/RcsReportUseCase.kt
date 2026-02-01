package com.messaging.usecase.rcs

import com.messaging.core.report.domain.Report
import com.messaging.core.report.domain.ReportPublisher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RcsReportUseCase(
    private val reportPublisher: ReportPublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun publish(report: Report) {
        log.info("Publishing RCS report: messageId={}, code={}", report.messageId, report.code)
        reportPublisher.publish(report)
    }
}
