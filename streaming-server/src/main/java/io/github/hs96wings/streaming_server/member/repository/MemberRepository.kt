package io.github.hs96wings.streaming_server.member.repository

import io.github.hs96wings.streaming_server.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MemberRepository : JpaRepository<Member, Long> {
    fun findByUserid(userid: String?): Optional<Member>
}
