package com.messaging.core.rcs.domain

object RcsReportCode {
    const val SUCCESS = "7000"
    const val UNKNOWN_ERROR = "7999"

    private val codeMap = mapOf(
        "9998" to "7101",  // RCS_API_ERROR
        "9999" to "7102",  // UNKNOWN_ERROR
        "400" to "7200",   // BAD_REQUEST
        "401" to "7201",   // UNAUTHORIZED
        "403" to "7202",   // FORBIDDEN
        "404" to "7203",   // NOT_FOUND
        "500" to "7300",   // INTERNAL_SERVER_ERROR
        "502" to "7301",   // BAD_GATEWAY
        "503" to "7302"    // SERVICE_UNAVAILABLE
    )

    fun from(result: RcsSendResult): String {
        if (result.success) return SUCCESS
        return result.resultCode?.let { codeMap[it] } ?: UNKNOWN_ERROR
    }
}
