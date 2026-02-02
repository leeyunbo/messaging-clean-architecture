package com.messaging.bootstrap.rcs.receiver

import com.messaging.bootstrap.rcs.receiver.handler.RcsServerHandler
import com.messaging.infrastructure.netty.codec.MessageDecoder
import com.messaging.infrastructure.netty.codec.MessageEncoder
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RcsNettyServer(
    private val rcsServerHandler: RcsServerHandler,
    @param:Value("\${server.netty.port}") private val port: Int
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val bossGroup = NioEventLoopGroup(1)
    private val workerGroup = NioEventLoopGroup()

    @PostConstruct
    fun start() {
        Thread {
            try {
                val bootstrap = ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel::class.java)
                    .childHandler(object : ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            ch.pipeline()
                                .addLast(MessageDecoder())
                                .addLast(MessageEncoder())
                                .addLast(rcsServerHandler)
                        }
                    })

                val future = bootstrap.bind(port).sync()
                log.info("RCS Netty Server started on port {}", port)
                future.channel().closeFuture().sync()
            } catch (e: Exception) {
                log.error("Failed to start Netty server", e)
            }
        }.start()
    }

    @PreDestroy
    fun stop() {
        log.info("Stopping RCS Netty Server")
        bossGroup.shutdownGracefully()
        workerGroup.shutdownGracefully()
    }
}
