package io.github.hs96wings.streaming_server.chat.dto

import io.github.hs96wings.streaming_server.chat.domain.ChatMessage

data class ChatMessageDto (
    val senderUserid: String,
    val message: String
) {
    companion object {
        @JvmStatic
        fun from(chatMessage: ChatMessage): ChatMessageDto {
            return ChatMessageDto(
                    senderUserid = chatMessage.member.userid,
                    message = chatMessage.message
            )
        }
    }
}