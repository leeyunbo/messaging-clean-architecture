package com.messaging.core.naver.domain

object NaverReportCode {
    const val SUCCESS = "8000"
    const val UNKNOWN_ERROR = "8999"

    private val codeMap = mapOf(
        "9998" to "8101",  // NAVER_API_ERROR
        "9999" to "8102",  // UNKNOWN_ERROR
        "400" to "8200",   // BAD_REQUEST
        "401" to "8201",   // UNAUTHORIZED
        "403" to "8202",   // FORBIDDEN
        "404" to "8203",   // NOT_FOUND
        "500" to "8300",   // INTERNAL_SERVER_ERROR
        "502" to "8301",   // BAD_GATEWAY
        "503" to "8302"    // SERVICE_UNAVAILABLE
    )

    fun from(result: NaverSendResult): String {
        if (result.success) return SUCCESS
        return result.resultCode?.let { codeMap[it] } ?: UNKNOWN_ERROR
    }
}
