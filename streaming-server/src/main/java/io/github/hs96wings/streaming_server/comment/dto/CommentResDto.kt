package io.github.hs96wings.streaming_server.comment.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.github.hs96wings.streaming_server.comment.domain.Comment
import io.github.hs96wings.streaming_server.common.domain.BaseEntity
import java.time.LocalDateTime

data class CommentResDto(
    val id: Long,
    val authorName: String,
    val content: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime?
) {
    constructor(comment: Comment): this (
        id = comment.id ?: throw IllegalStateException("Comment ID cannot be null for DTO conversion"),
        authorName = comment.author.userid,
        content = comment.content,
        createdAt = comment.createdAt
    )
}