package io.github.hs96wings.streaming_server.comment.service;

import io.github.hs96wings.streaming_server.comment.domain.Comment;
import io.github.hs96wings.streaming_server.comment.dto.CommentResDto;
import io.github.hs96wings.streaming_server.comment.dto.CommentSaveReqDto;
import io.github.hs96wings.streaming_server.comment.repository.CommentRepository;
import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.member.repository.MemberRepository;
import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;
    private final MemberRepository memberRepository;

    public CommentService(CommentRepository commentRepository, VideoRepository videoRepository, MemberRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.videoRepository = videoRepository;
        this.memberRepository = memberRepository;
    }

    public CommentResDto addComment(CommentSaveReqDto commentSaveReqDto, String username) {
        Video video = videoRepository.findById(commentSaveReqDto.getVideoId())
                .orElseThrow(() -> new EntityNotFoundException("영상이 없습니다"));
        Member member = memberRepository.findByUserid(username)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다"));

        Comment comment = Comment.builder()
                .video(video)
                .author(member)
                .content(commentSaveReqDto.getContent())
                .build();

        commentRepository.save(comment);

        return new CommentResDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResDto> getComments(Long videoId) {
        return commentRepository.findByVideoIdOrderByCreatedAtAsc(videoId)
                .stream()
                .map(CommentResDto::new)
                .toList();
    }

    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다"));

        if (!comment.getAuthor().getUserid().equals(username)) {
            throw new org.springframework.security.access.AccessDeniedException("작성자만 삭제할 수 있습니다");
        }

        commentRepository.delete(comment);
    }
}
