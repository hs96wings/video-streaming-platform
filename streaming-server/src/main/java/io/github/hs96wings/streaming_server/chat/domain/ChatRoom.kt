package io.github.hs96wings.streaming_server.chat.domain

import io.github.hs96wings.streaming_server.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
open class ChatRoom @JvmOverloads constructor (
    @Column(nullable = false)
    var name: String,
    @Column(unique = true)
    var roomKey: String? = null,
    var isGroupChat: Boolean = false
): BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long? = null

    @OneToMany(mappedBy = "chatRoom", cascade = [CascadeType.REMOVE])
    val chatParticipants: MutableList<ChatParticipant> = mutableListOf()

    @OneToMany(mappedBy = "chatRoom", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val chatMessages: MutableList<ChatMessage> = mutableListOf()
}