package com.messaging.provider

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SendResultTest {

    @Test
    fun `success 팩토리 메서드 - 기본값`() {
        val result = SendResult.success()

        assertTrue(result.success)
        assertEquals("0000", result.resultCode)
        assertEquals("성공", result.resultMessage)
        assertFalse(result.retryable)
    }

    @Test
    fun `success 팩토리 메서드 - 커스텀 메시지`() {
        val result = SendResult.success("발송 완료")

        assertTrue(result.success)
        assertEquals("0000", result.resultCode)
        assertEquals("발송 완료", result.resultMessage)
        assertFalse(result.retryable)
    }

    @Test
    fun `fail 팩토리 메서드 - 기본값`() {
        val result = SendResult.fail("E001", "에러 발생")

        assertFalse(result.success)
        assertEquals("E001", result.resultCode)
        assertEquals("에러 발생", result.resultMessage)
        assertFalse(result.retryable)
    }

    @Test
    fun `fail 팩토리 메서드 - retryable true`() {
        val result = SendResult.fail("TIMEOUT", "타임아웃", retryable = true)

        assertFalse(result.success)
        assertEquals("TIMEOUT", result.resultCode)
        assertEquals("타임아웃", result.resultMessage)
        assertTrue(result.retryable)
    }

    @Test
    fun `직접 생성자 사용`() {
        val result = SendResult(
            success = true,
            resultCode = "CUSTOM",
            resultMessage = "커스텀 결과",
            retryable = true
        )

        assertTrue(result.success)
        assertEquals("CUSTOM", result.resultCode)
        assertEquals("커스텀 결과", result.resultMessage)
        assertTrue(result.retryable)
    }
}
