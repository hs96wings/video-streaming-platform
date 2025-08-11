package io.github.hs96wings.streaming_server.chat.repository

import io.github.hs96wings.streaming_server.chat.domain.ChatParticipant
import io.github.hs96wings.streaming_server.chat.domain.ChatRoom
import io.github.hs96wings.streaming_server.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChatParticipantRepository : JpaRepository<ChatParticipant, Long> {
    fun findByChatRoom(chatRoom: ChatRoom): List<ChatParticipant>
    fun findByChatRoomAndMember(chatRoom: ChatRoom, member: Member): Optional<ChatParticipant>
    fun findAllByMember(member: Member): List<ChatParticipant>
}
