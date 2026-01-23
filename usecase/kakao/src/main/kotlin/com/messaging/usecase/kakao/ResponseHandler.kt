package com.messaging.usecase.kakao

import com.messaging.core.kakao.domain.AlimtalkPollingItem
import com.messaging.core.kakao.domain.AlimtalkPollingStore
import com.messaging.core.kakao.domain.KakaoSendResult
import com.messaging.core.kakao.domain.ResponseMethod
import com.messaging.core.report.domain.Report
import com.messaging.core.report.domain.ReportPublisher
import org.slf4j.LoggerFactory

/**
 * 응답 방식별 처리 전략
 */
interface ResponseHandler {
    suspend fun handle(messageId: String, result: KakaoSendResult)
}

/**
 * PUSH/REALTIME 응답 처리
 * - 결과를 리포트 큐에 발행
 */
class SyncResponseHandler(
    private val reportPublisher: ReportPublisher
) : ResponseHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun handle(messageId: String, result: KakaoSendResult) {
        val report = Report(
            messageId = messageId,
            code = result.resultCode ?: "UNKNOWN"
        )
        reportPublisher.publish(report)
        log.info("Report published: messageId={}, code={}", messageId, report.code)
    }
}

/**
 * POLLING 응답 처리
 * - 성공(0000): 폴링 저장소에 저장 (폴링 모듈에서 주기적으로 결과 조회)
 * - 실패: 리포트 큐에 발행
 */
class PollingResponseHandler(
    private val pollingStore: AlimtalkPollingStore,
    private val reportPublisher: ReportPublisher
) : ResponseHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun handle(messageId: String, result: KakaoSendResult) {
        val serialNumber = result.serialNumber
        if (result.success && serialNumber != null) {
            pollingStore.save(AlimtalkPollingItem(
                messageId = messageId,
                serialNumber = serialNumber
            ))
            log.info("Saved to polling store: messageId={}, serialNumber={}", messageId, serialNumber)
        } else {
            val report = Report(
                messageId = messageId,
                code = result.resultCode ?: "UNKNOWN"
            )
            reportPublisher.publish(report)
            log.info("Report published (polling failed): messageId={}, code={}", messageId, report.code)
        }
    }
}

/**
 * ResponseMethod → Handler 매핑 팩토리
 */
class ResponseHandlerFactory(
    reportPublisher: ReportPublisher,
    pollingStore: AlimtalkPollingStore
) {
    private val syncHandler = SyncResponseHandler(reportPublisher)
    private val pollingHandler = PollingResponseHandler(pollingStore, reportPublisher)

    fun getHandler(method: ResponseMethod): ResponseHandler = when (method) {
        ResponseMethod.PUSH, ResponseMethod.REALTIME -> syncHandler
        ResponseMethod.POLLING -> pollingHandler
    }
}
