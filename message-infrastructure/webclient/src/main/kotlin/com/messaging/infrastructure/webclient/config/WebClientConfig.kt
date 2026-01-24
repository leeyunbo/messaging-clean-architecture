package com.messaging.infrastructure.webclient.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * HTTP 클라이언트 기본 설정값
 */
object HttpDefaults {
    /** 기본 연결 타임아웃 (ms) */
    const val DEFAULT_CONNECT_TIMEOUT_MS = 5_000

    /** 기본 읽기 타임아웃 (ms) */
    const val DEFAULT_READ_TIMEOUT_MS = 10_000

    /** 기본 쓰기 타임아웃 (ms) */
    const val DEFAULT_WRITE_TIMEOUT_MS = 10_000
}

/**
 * WebClient 공통 설정
 */
@Configuration
class WebClientConfig {

    @Value("\${webclient.connect-timeout:${HttpDefaults.DEFAULT_CONNECT_TIMEOUT_MS}}")
    private var connectTimeout: Int = HttpDefaults.DEFAULT_CONNECT_TIMEOUT_MS

    @Value("\${webclient.read-timeout:${HttpDefaults.DEFAULT_READ_TIMEOUT_MS}}")
    private var readTimeout: Int = HttpDefaults.DEFAULT_READ_TIMEOUT_MS

    @Value("\${webclient.write-timeout:${HttpDefaults.DEFAULT_WRITE_TIMEOUT_MS}}")
    private var writeTimeout: Int = HttpDefaults.DEFAULT_WRITE_TIMEOUT_MS

    @Bean
    fun defaultWebClient(): WebClient {
        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
            .responseTimeout(Duration.ofMillis(readTimeout.toLong()))
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(readTimeout.toLong(), TimeUnit.MILLISECONDS))
                conn.addHandlerLast(WriteTimeoutHandler(writeTimeout.toLong(), TimeUnit.MILLISECONDS))
            }

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }
}
