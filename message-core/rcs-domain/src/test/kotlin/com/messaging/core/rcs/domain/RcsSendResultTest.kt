package com.messaging.core.rcs.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RcsSendResultTest {

    @Test
    fun `success 팩토리 - requestId 포함`() {
        val result = RcsSendResult.success("req-001")

        assertTrue(result.success)
        assertEquals("req-001", result.requestId)
        assertNull(result.resultCode)
        assertNull(result.resultMessage)
    }

    @Test
    fun `success 팩토리 - requestId 없음`() {
        val result = RcsSendResult.success()

        assertTrue(result.success)
        assertNull(result.requestId)
    }

    @Test
    fun `fail 팩토리`() {
        val result = RcsSendResult.fail("400", "Bad Request")

        assertFalse(result.success)
        assertEquals("400", result.resultCode)
        assertEquals("Bad Request", result.resultMessage)
        assertNull(result.requestId)
    }

    @Test
    fun `unknownError 팩토리`() {
        val result = RcsSendResult.unknownError()

        assertFalse(result.success)
        assertEquals("9999", result.resultCode)
        assertEquals("UNKNOWN_ERROR", result.resultMessage)
    }

    @Test
    fun `rcsApiError 팩토리`() {
        val result = RcsSendResult.rcsApiError()

        assertFalse(result.success)
        assertEquals("9998", result.resultCode)
        assertEquals("RCS_API_ERROR", result.resultMessage)
    }
}
