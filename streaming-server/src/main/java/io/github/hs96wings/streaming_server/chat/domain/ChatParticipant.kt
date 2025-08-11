package io.github.hs96wings.streaming_server.chat.domain

import io.github.hs96wings.streaming_server.common.domain.BaseEntity
import io.github.hs96wings.streaming_server.member.domain.Member
import jakarta.persistence.*

@Entity
open class ChatParticipant @JvmOverloads constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    var chatRoom: ChatRoom,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member
): BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long? = null

    @OneToMany(mappedBy = "chatParticipant", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    var readStatuses: MutableList<ReadStatus> = mutableListOf()
}