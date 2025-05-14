package io.github.hs96wings.streaming_server.comment.integration;

import io.github.hs96wings.streaming_server.Comment.domain.Comment;
import io.github.hs96wings.streaming_server.Comment.dto.CommentResDto;
import io.github.hs96wings.streaming_server.Comment.dto.CommentSaveReqDto;
import io.github.hs96wings.streaming_server.Comment.repository.CommentRepository;
import io.github.hs96wings.streaming_server.Comment.service.CommentService;
import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.member.dto.MemberSaveReqDto;
import io.github.hs96wings.streaming_server.member.repository.MemberRepository;
import io.github.hs96wings.streaming_server.member.service.MemberService;
import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.dto.VideoSaveReqDto;
import io.github.hs96wings.streaming_server.video.repository.VideoRepository;
import io.github.hs96wings.streaming_server.video.service.VideoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class CommentServiceIntegrationTest {
    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    VideoService videoService;

    private Member member;
    private Video video;
    private CommentResDto comment;

    private static final Logger log = LoggerFactory.getLogger(CommentServiceIntegrationTest.class);

    @BeforeEach
    void setUp() {
        member = memberService.create(new MemberSaveReqDto("testUser", "1234"));
        video = videoService.upload(new VideoSaveReqDto("테스트 제목", "테스트 설명",
                new MockMultipartFile("file", "test.mp4", MediaType.APPLICATION_OCTET_STREAM_VALUE, "dummy content".getBytes())));
        comment = commentService.addComment(new CommentSaveReqDto(video.getId(), "테스트 댓글"), member.getUserid());
    }

    @Test
    @DisplayName("DB에 댓글을 저장한다")
    void publishComment_shouldPersist() {
        // given
        Long savedId = comment.getId();

        // when
        Comment findComment = commentRepository.findById(savedId)
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 댓글입니다"));

        // then
        assertAll("Comment Entity 검증",
                () -> assertThat(findComment.getId()).isEqualTo(comment.getId()),
                () -> assertThat(findComment.getAuthor().getUserid()).isEqualTo(comment.getAuthorName()),
                () -> assertThat(findComment.getVideo().getId()).isEqualTo(video.getId()),
                () -> assertThat(findComment.getContent()).isEqualTo(comment.getContent())
        );
    }

    @Test
    @DisplayName("DB에서 댓글을 삭제한다")
    void deleteComment_shouldPersist() {
        // when
        commentService.deleteComment(comment.getId(), member.getUserid());

        // then: repository.findById가 빈 Optional을 반환했는지 검증
        Optional<Comment> findComment = commentRepository.findById(comment.getId());
        assertThat(findComment).isEmpty();
    }

    @Test
    @DisplayName("작성자가 다르면 댓글을 삭제하지 못한다")
    void deleteCommentNotFoundUser_shouldPersist() {
        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(comment.getId(), "newUser")
        ).isInstanceOf(AccessDeniedException.class)
        .hasMessage("작성자만 삭제할 수 있습니다");
    }
}
