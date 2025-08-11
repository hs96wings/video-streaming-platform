package io.github.hs96wings.streaming_server.chat.domain

import io.github.hs96wings.streaming_server.common.domain.BaseEntity
import io.github.hs96wings.streaming_server.member.domain.Member
import jakarta.persistence.*

@Entity
open class ChatMessage @JvmOverloads constructor (
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    var chatRoom: ChatRoom,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @Column(nullable = false, length = 500)
    var message: String,
): BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long? = null // id는 불변이므로 val, DB 생성 전까지 null일 수 있으므로 Nullable

    @OneToMany(mappedBy = "chatMessage", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    var readStatus: MutableList<ReadStatus> = mutableListOf(); // 비어있는 mutable list로 초기화
}