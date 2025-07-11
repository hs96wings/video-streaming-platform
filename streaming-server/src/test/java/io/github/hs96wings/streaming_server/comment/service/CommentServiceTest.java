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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private CommentService commentService;

    private Member member;
    private Video video;
    private Comment comment;

    @BeforeEach
    void setup() {
        member = Member.builder().id(1L).userid("testUser").password("1234").build();
        video = Video.builder().id(1L).title("testTitle").build();
        comment = Comment.builder().id(1L).video(video).author(member).content("test").build();
    }
    private static final Logger log = LoggerFactory.getLogger(CommentServiceTest.class);

    @Test
    @DisplayName("댓글 등록 성공")
    void testPublishComment_Success() throws Exception {
        // given
        CommentSaveReqDto commentSaveReqDto = new CommentSaveReqDto(1L, "content");

        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(memberRepository.findByUserid(member.getUserid())).thenReturn(Optional.of(member));
        when(commentRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // when
        CommentResDto commentResDto = commentService.addComment(commentSaveReqDto, member.getUserid());

        // then
        assertThat(commentResDto.getAuthorName()).isEqualTo(member.getUserid());
        assertThat(commentResDto.getContent()).isEqualTo(commentSaveReqDto.getContent());

        verify(videoRepository, times(1)).findById(1L);
        verify(memberRepository, times(1)).findByUserid("testUser");
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 목록 불러오기")
    void testGetCommentList() throws Exception {
        // given
        // static member, video, comment 사용
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);

        when(commentRepository.findByVideoIdOrderByCreatedAtAsc(any())).thenReturn(comments);

        // when
        List<CommentResDto> commentResDtos = commentService.getComments(1L);
        CommentResDto result = commentResDtos.get(0);

        // then
        assertThat(commentResDtos.size()).isEqualTo(1);
        assertThat(result.getAuthorName()).isEqualTo(member.getUserid());
        assertThat(result.getContent()).isEqualTo("test");
    }

    @Test
    @DisplayName("댓글 삭제하기")
    void testDeleteComment() throws Exception {
        // given
        // static member, video 사용
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        // when
        commentService.deleteComment(1L, member.getUserid());

        // then
        verify(commentRepository, times(1)).delete(comment);

    }

    @Test
    @DisplayName("존재하지 않는 댓글을 삭제할 수 없다")
    void testDeleteComment_fail_NotFoundComment() throws Exception {
        // given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            commentService.deleteComment(1L, member.getUserid());
        });

        // then
        assertThat(exception).hasMessage("존재하지 않는 댓글입니다");
        verify(commentRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("작성자가 아니면 댓글을 삭제할 수 있다")
    void testDeleteComment_fail_AccessDenied() throws Exception {
        // given
        Member otherMember = Member.builder().id(1L).userid("testUser2").password("1234").build();
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        // when
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            commentService.deleteComment(1L, otherMember.getUserid());
        });

        // then
        assertThat(exception).hasMessage("작성자만 삭제할 수 있습니다");
        verify(commentRepository, times(1)).findById(anyLong());
    }
}
