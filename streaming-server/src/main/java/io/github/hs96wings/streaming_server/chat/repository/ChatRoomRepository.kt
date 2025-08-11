package io.github.hs96wings.streaming_server.chat.repository

import io.github.hs96wings.streaming_server.chat.domain.ChatRoom
import io.github.hs96wings.streaming_server.chat.dto.MyChatListResDto
import io.github.hs96wings.streaming_server.member.domain.Member
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChatRoomRepository : JpaRepository<ChatRoom, Long> {
    fun findByIsGroupChat(isGroupChat: Boolean): List<ChatRoom>
    fun findByRoomKey(roomKey: String): ChatRoom?
    @Query("""
        SELECT new io.github.hs96wings.streaming_server.chat.dto.MyChatListResDto(
                   cr.id, 
                   cr.name, 
                   cr.isGroupChat, 
                   (SELECT COUNT(rs.id) FROM ReadStatus rs WHERE rs.chatParticipant = cp AND rs.isRead = false)
               )
        FROM ChatParticipant cp
        JOIN cp.chatRoom cr
        WHERE cp.member = :member
    """)
    fun findMyChatList(@Param("member") member: Member): List<MyChatListResDto>
}
