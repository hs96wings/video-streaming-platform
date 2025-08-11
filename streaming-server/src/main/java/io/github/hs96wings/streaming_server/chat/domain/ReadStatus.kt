package io.github.hs96wings.streaming_server.chat.domain

import io.github.hs96wings.streaming_server.common.domain.BaseEntity
import io.github.hs96wings.streaming_server.common.domain.BaseTimeEntity
import jakarta.persistence.*

@Entity
open class ReadStatus @JvmOverloads constructor(
    @Column(nullable = false)
    var isRead: Boolean = false,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    var chatRoom: ChatRoom,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_participant_id", nullable = false)
    var chatParticipant: ChatParticipant,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_message_id", nullable = false)
    var chatMessage: ChatMessage
): BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long? = null

    fun updateIsRead(isRead: Boolean) {
        this.isRead = isRead
    }
}