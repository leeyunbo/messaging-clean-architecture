package com.messaging.core.rcs.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RcsReportCodeTest {

    @Test
    fun `성공 결과 - SUCCESS 코드 반환`() {
        val result = RcsSendResult.success("req-001")

        assertEquals("7000", RcsReportCode.from(result))
    }

    @Test
    fun `RCS API 에러 - 7101 반환`() {
        val result = RcsSendResult(
            success = false,
            resultCode = "9998"
        )

        assertEquals("7101", RcsReportCode.from(result))
    }

    @Test
    fun `UNKNOWN_ERROR - 7102 반환`() {
        val result = RcsSendResult(
            success = false,
            resultCode = "9999"
        )

        assertEquals("7102", RcsReportCode.from(result))
    }

    @Test
    fun `BAD_REQUEST - 7200 반환`() {
        val result = RcsSendResult.fail("400", "Bad Request")

        assertEquals("7200", RcsReportCode.from(result))
    }

    @Test
    fun `UNAUTHORIZED - 7201 반환`() {
        val result = RcsSendResult.fail("401", "Unauthorized")

        assertEquals("7201", RcsReportCode.from(result))
    }

    @Test
    fun `FORBIDDEN - 7202 반환`() {
        val result = RcsSendResult.fail("403", "Forbidden")

        assertEquals("7202", RcsReportCode.from(result))
    }

    @Test
    fun `NOT_FOUND - 7203 반환`() {
        val result = RcsSendResult.fail("404", "Not Found")

        assertEquals("7203", RcsReportCode.from(result))
    }

    @Test
    fun `INTERNAL_SERVER_ERROR - 7300 반환`() {
        val result = RcsSendResult.fail("500", "Internal Server Error")

        assertEquals("7300", RcsReportCode.from(result))
    }

    @Test
    fun `BAD_GATEWAY - 7301 반환`() {
        val result = RcsSendResult.fail("502", "Bad Gateway")

        assertEquals("7301", RcsReportCode.from(result))
    }

    @Test
    fun `SERVICE_UNAVAILABLE - 7302 반환`() {
        val result = RcsSendResult.fail("503", "Service Unavailable")

        assertEquals("7302", RcsReportCode.from(result))
    }

    @Test
    fun `매핑되지 않은 코드 - UNKNOWN_ERROR 반환`() {
        val result = RcsSendResult.fail("999", "Unknown")

        assertEquals("7999", RcsReportCode.from(result))
    }

    @Test
    fun `resultCode가 null인 실패 - UNKNOWN_ERROR 반환`() {
        val result = RcsSendResult(
            success = false,
            resultCode = null
        )

        assertEquals("7999", RcsReportCode.from(result))
    }
}
