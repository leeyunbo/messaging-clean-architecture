package com.messaging.platform.rcs.webhook

import com.messaging.core.rcs.domain.RcsReportCode
import com.messaging.core.report.domain.Report

/**
 * RCS 웹훅 파싱 및 Report 변환
 */
object RcsWebhookParser {

    fun toReport(payload: RcsWebhookPayload): Report {
        val code = mapToReportCode(payload)
        return Report(
            messageId = payload.messageId,
            code = code
        )
    }

    private fun mapToReportCode(payload: RcsWebhookPayload): String {
        return when (payload.status) {
            RcsWebhookStatus.DELIVERED -> RcsReportCode.SUCCESS
            RcsWebhookStatus.READ -> RcsReportCode.SUCCESS
            RcsWebhookStatus.FAILED -> mapErrorCode(payload.resultCode)
            RcsWebhookStatus.EXPIRED -> "7400"  // EXPIRED
            else -> RcsReportCode.UNKNOWN_ERROR
        }
    }

    private fun mapErrorCode(resultCode: String): String {
        return when (resultCode) {
            "1001" -> "7501"  // INVALID_RECIPIENT
            "1002" -> "7502"  // RECIPIENT_NOT_RCS_USER
            "1003" -> "7503"  // MESSAGE_TOO_LONG
            "2001" -> "7601"  // NETWORK_ERROR
            "2002" -> "7602"  // TIMEOUT
            "3001" -> "7701"  // BLOCKED_BY_USER
            else -> RcsReportCode.UNKNOWN_ERROR
        }
    }
}
