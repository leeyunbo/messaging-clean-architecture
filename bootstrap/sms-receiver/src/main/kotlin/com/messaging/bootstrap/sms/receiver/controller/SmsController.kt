package com.messaging.bootstrap.sms.receiver.controller

import com.messaging.bootstrap.sms.receiver.dto.SendSmsRequest
import com.messaging.bootstrap.sms.receiver.dto.SendSmsResponse
import com.messaging.bootstrap.sms.receiver.service.SmsReceiveService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/sms")
class SmsController(
    private val smsReceiveService: SmsReceiveService
) {

    @PostMapping("/send")
    @ResponseStatus(HttpStatus.ACCEPTED)
    suspend fun send(
        @RequestHeader("X-Partner-Id") partnerId: String,
        @Valid @RequestBody request: SendSmsRequest
    ): SendSmsResponse {
        return smsReceiveService.receive(partnerId, request)
    }

    @GetMapping("/status/{messageId}")
    suspend fun getStatus(
        @RequestHeader("X-Partner-Id") partnerId: String,
        @PathVariable messageId: String
    ): Map<String, Any?> {
        return smsReceiveService.getStatus(partnerId, messageId)
    }
}
