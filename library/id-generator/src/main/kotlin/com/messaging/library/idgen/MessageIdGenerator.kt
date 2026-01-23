package com.messaging.library.idgen

import com.github.f4b6a3.ulid.UlidCreator

/**
 * 메시지 ID 생성기
 * ULID (Universally Unique Lexicographically Sortable Identifier) 사용
 * - 시간순 정렬 가능
 * - UUID보다 짧은 26자
 * - URL-safe
 */
object MessageIdGenerator {

    /**
     * 새로운 메시지 ID 생성
     * @return ULID 형식의 고유 ID (예: 01ARZ3NDEKTSV4RRFFQ69G5FAV)
     */
    fun generate(): String {
        return UlidCreator.getUlid().toString()
    }

    /**
     * 프리픽스가 붙은 메시지 ID 생성
     * @param prefix 프리픽스 (예: "MSG", "SMS")
     * @return 프리픽스-ULID 형식의 ID (예: MSG-01ARZ3NDEKTSV4RRFFQ69G5FAV)
     */
    fun generateWithPrefix(prefix: String): String {
        return "$prefix-${generate()}"
    }
}
