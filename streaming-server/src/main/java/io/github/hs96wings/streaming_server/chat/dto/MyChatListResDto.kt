package io.github.hs96wings.streaming_server.chat.dto

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor

data class MyChatListResDto @JvmOverloads constructor(
    val roomId: Long,
    val roomName: String,
    val isGroupChat: Boolean,
    val unReadCount: Long
)
