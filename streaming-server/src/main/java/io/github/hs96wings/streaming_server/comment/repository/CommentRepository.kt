package io.github.hs96wings.streaming_server.comment.repository

import io.github.hs96wings.streaming_server.comment.domain.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByVideoIdOrderByCreatedAtAsc(videoId: Long): List<Comment>
}
