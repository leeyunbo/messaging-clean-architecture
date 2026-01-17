package com.messaging.provider

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class VendorRequestTest {

    @Test
    fun `body만 있는 요청 생성`() {
        val request = VendorRequest(
            body = mapOf("msgId" to "test-001", "content" to "테스트")
        )

        assertEquals(2, request.body.size)
        assertEquals("test-001", request.body["msgId"])
        assertEquals("테스트", request.body["content"])
        assertTrue(request.headers.isEmpty())
    }

    @Test
    fun `body와 headers 모두 있는 요청 생성`() {
        val request = VendorRequest(
            body = mapOf("msgId" to "test-001"),
            headers = mapOf("X-Serial-Number" to "serial-001", "Authorization" to "Bearer token")
        )

        assertEquals(1, request.body.size)
        assertEquals(2, request.headers.size)
        assertEquals("serial-001", request.headers["X-Serial-Number"])
        assertEquals("Bearer token", request.headers["Authorization"])
    }

    @Test
    fun `빈 body 허용`() {
        val request = VendorRequest(body = emptyMap())

        assertTrue(request.body.isEmpty())
        assertTrue(request.headers.isEmpty())
    }

    @Test
    fun `nullable 값 포함`() {
        val request = VendorRequest(
            body = mapOf("field1" to "value", "field2" to null),
            headers = mapOf("header1" to null)
        )

        assertEquals("value", request.body["field1"])
        assertNull(request.body["field2"])
        assertNull(request.headers["header1"])
    }
}
