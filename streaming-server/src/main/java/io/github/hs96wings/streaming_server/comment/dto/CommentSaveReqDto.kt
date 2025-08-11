package io.github.hs96wings.streaming_server.comment.dto

data class CommentSaveReqDto (
    val videoId: Long,
    val content: String
)
