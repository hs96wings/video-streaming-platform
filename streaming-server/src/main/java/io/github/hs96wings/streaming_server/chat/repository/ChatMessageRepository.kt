package io.github.hs96wings.streaming_server.chat.repository

import io.github.hs96wings.streaming_server.chat.domain.ChatMessage
import io.github.hs96wings.streaming_server.chat.domain.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {
    fun findByChatRoomOrderByCreatedAtAsc(chatRoom: ChatRoom): List<ChatMessage>
}
