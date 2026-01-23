package com.messaging.bootstrap.kakao

import com.messaging.core.kakao.domain.AlimtalkRequest
import com.messaging.core.kakao.domain.ResponseMethod
import com.messaging.usecase.kakao.AlimtalkSendUseCase
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

/**
 * 알림톡 발송 메시지 소비자 (Spring AMQP)
 * RabbitMQ에서 메시지를 꺼내 AlimtalkSendUseCase 호출
 */
@Component
class AlimtalkMessageConsumer(
    private val alimtalkSendUseCase: AlimtalkSendUseCase
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 큐 이름은 partner.id 환경변수로 결정
     * application.yml: rabbitmq.queue = alimtalk-send-queue-${PARTNER_ID}
     */
    @RabbitListener(queues = ["\${rabbitmq.queue}"])
    fun onMessage(message: AlimtalkMessage) {
        log.info("Received message: messageId={}", message.messageId)

        runBlocking {
            try {
                val request = AlimtalkRequest(
                    messageId = message.messageId,
                    responseMethod = ResponseMethod.valueOf(message.responseMethod),
                    timeout = message.timeout,
                    variables = message.variables
                )

                val result = alimtalkSendUseCase.send(request)
                log.info("Send completed: messageId={}, success={}", message.messageId, result.success)
            } catch (e: Exception) {
                log.error("Failed to process message: messageId={}, error={}",
                    message.messageId, e.message, e)
            }
        }
    }
}

/**
 * 큐에서 수신하는 메시지 형식
 */
data class AlimtalkMessage(
    val messageId: String,
    val responseMethod: String = "PUSH",
    val timeout: Long? = null,
    val variables: Map<String, Any?> = emptyMap()
)
