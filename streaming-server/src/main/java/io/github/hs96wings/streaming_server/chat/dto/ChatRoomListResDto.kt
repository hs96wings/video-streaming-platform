package io.github.hs96wings.streaming_server.chat.dto

import io.github.hs96wings.streaming_server.chat.domain.ChatRoom

data class ChatRoomListResDto (
    val roomId: Long,
    val roomName: String
) {
    companion object {
        @JvmStatic
        fun from(chatRoom: ChatRoom): ChatRoomListResDto {
            return ChatRoomListResDto(
                    roomId = chatRoom.id ?: throw IllegalStateException("chatRoom ID cannot be null"),
                    roomName = chatRoom.name
            )
        }
    }
}
