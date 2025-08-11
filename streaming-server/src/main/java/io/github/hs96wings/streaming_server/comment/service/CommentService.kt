package io.github.hs96wings.streaming_server.comment.service

import io.github.hs96wings.streaming_server.comment.domain.Comment
import io.github.hs96wings.streaming_server.comment.dto.CommentResDto
import io.github.hs96wings.streaming_server.comment.dto.CommentSaveReqDto
import io.github.hs96wings.streaming_server.comment.repository.CommentRepository
import io.github.hs96wings.streaming_server.member.domain.Member
import io.github.hs96wings.streaming_server.member.repository.MemberRepository
import io.github.hs96wings.streaming_server.video.domain.Video
import io.github.hs96wings.streaming_server.video.repository.VideoRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CommentService(
    private val commentRepository: CommentRepository,
    private val videoRepository: VideoRepository,
    private val memberRepository: MemberRepository
) {
    fun addComment(commentSaveReqDto: CommentSaveReqDto, userid: String): CommentResDto {
        val video = findVideoById(commentSaveReqDto.videoId)
        val member = findMemberByUserId(userid)

        val comment = Comment(
            commentSaveReqDto.content,
            video,
            member
        )

        val savedComment: Comment = commentRepository.save(comment)
        return CommentResDto.from(savedComment)
    }

    fun getComments(videoId: Long): List<CommentResDto> {
        return commentRepository.findByVideoIdOrderByCreatedAtAsc(videoId).map(CommentResDto::from)
    }

    @Transactional
    fun deleteComment(commentId: Long, username: String) {
        val comment: Comment = findCommentById(commentId)

        if (comment.author.userid != username) {
            throw AccessDeniedException("작성자만 삭제할 수 있습니다")
        }

        commentRepository.delete(comment)
    }

    private fun findVideoById(videoId: Long): Video {
        return videoRepository.findById(videoId)
            .orElse(null) ?: throw IllegalArgumentException("해당 영상이 존재하지 않습니다. id=$videoId")
    }

    private fun findMemberByUserId(userid: String): Member {
        return memberRepository.findByUserid(userid)
            .orElse(null) ?: throw EntityNotFoundException("존재하지 않는 유저입니다")
    }

    private fun findCommentById(commentId: Long): Comment {
        return commentRepository.findById(commentId)
            .orElse(null) ?: throw EntityNotFoundException("존재하지 않는 댓글입니다")
    }
}
