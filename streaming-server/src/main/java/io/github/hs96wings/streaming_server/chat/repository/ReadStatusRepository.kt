package io.github.hs96wings.streaming_server.chat.repository

import io.github.hs96wings.streaming_server.chat.domain.ChatParticipant
import io.github.hs96wings.streaming_server.chat.domain.ReadStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReadStatusRepository : JpaRepository<ReadStatus, Long> {
    fun findByChatParticipant(chatParticipant: ChatParticipant): List<ReadStatus>
    fun countByChatParticipantAndIsReadFalse(chatParticipant: ChatParticipant): Long
}
