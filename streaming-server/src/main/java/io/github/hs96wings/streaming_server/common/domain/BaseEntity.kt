package io.github.hs96wings.streaming_server.common.domain

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity: BaseTimeEntity() {
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    abstract val id: Long?

    // 공통 로직을 BaseEntity 로 이동
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        // id가 null 이거나, 프록시 객체일 경우 클래스가 다르므로 false 처리
        if (id == null || other !is BaseEntity || javaClass != other.javaClass) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        // id가 없으면 31(소수)를 반환
        return id?.hashCode() ?: 31

    }

    override fun toString(): String {
        // 클래스 이름과 id를 출력하여, Lazy-Loading 문제를 피하고 명확한 정보 제공
        return "${this.javaClass.simpleName}(id=$id)"
    }
}