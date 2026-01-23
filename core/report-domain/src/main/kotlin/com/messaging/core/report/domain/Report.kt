package com.messaging.core.report.domain

/**
 * 발송 결과 리포트
 */
data class Report(
    val messageId: String,  // 클라이언트가 보낸 메시지 ID
    val code: String        // 응답 코드
)
