package com.messaging.common.domain

import org.hibernate.internal.util.collections.CollectionHelper.listOf

enum class MessageStatus(val description: String) {
    // 초기 상태
    RECEIVED("접수됨"),

    // 처리 중
    PROCESSING("처리 중"),
    SENT("발송됨"),

    // 최종 상태
    DELIVERED("전달 완료"),
    FAILED("실패"),
    EXPIRED("만료됨");

    fun isTerminal(): Boolean = this in listOf(DELIVERED, FAILED, EXPIRED)
}
