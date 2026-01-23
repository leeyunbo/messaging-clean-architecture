package com.messaging.core.kakao.domain

/**
 * 알림톡 폴링 대상 저장소 Port
 * POLLING 응답방식일 때 성공(0000) 건을 저장
 * 폴링 모듈에서 주기적으로 조회하여 카카오 폴링 API 호출
 */
interface AlimtalkPollingStore {
    suspend fun save(item: AlimtalkPollingItem)
}

/**
 * 폴링 대상 아이템
 */
data class AlimtalkPollingItem(
    val messageId: String,
    val serialNumber: String  // 카카오 폴링 API 호출 시 필요
)
