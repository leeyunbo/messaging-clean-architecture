package com.messaging.usecase.naver

import com.messaging.core.naver.domain.NaverProvider
import com.messaging.core.naver.domain.NaverReportCode
import com.messaging.core.naver.domain.NaverSendRequest
import com.messaging.core.naver.domain.NaverSendResult
import com.messaging.core.report.domain.Report
import com.messaging.core.report.domain.ReportPublisher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class NaverSendUseCase(
    private val naverProvider: NaverProvider,
    private val reportPublisher: ReportPublisher
) {
    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun send(request: NaverSendRequest): NaverSendResult {
        log.info("Processing Naver send: messageId={}", request.messageId)

        val result = naverProvider.send(request)
        val report = Report(
            messageId = request.messageId,
            code = NaverReportCode.from(result)
        )
        reportPublisher.publish(report)
        log.info("Report published: messageId={}, code={}", request.messageId, report.code)

        return result
    }
}
