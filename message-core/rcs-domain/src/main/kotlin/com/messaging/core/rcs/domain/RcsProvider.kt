package com.messaging.core.rcs.domain

/**
 * RCS Provider 인터페이스 (Port)
 */
interface RcsProvider {
    suspend fun sendStandalone(request: RcsStandaloneRequest): RcsSendResult
    suspend fun sendCarousel(request: RcsCarouselRequest): RcsSendResult
}
