package io.github.hs96wings.streaming_server.chat.repository

import io.github.hs96wings.streaming_server.chat.domain.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ChatRoomRepository : JpaRepository<ChatRoom, Long> {
    fun findByIsGroupChat(isGroupChat: Boolean): List<ChatRoom>
    fun findByRoomKey(roomKey: String): ChatRoom?
}
