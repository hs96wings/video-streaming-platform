package io.github.hs96wings.streaming_server.comment.repository;

import io.github.hs96wings.streaming_server.comment.domain.Comment;
import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.member.repository.MemberRepository;
import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private VideoRepository videoRepository;

    private Member member;
    private Video video;
    private Comment comment;

    @BeforeEach
    void setup() {
        member = Member.builder().userid("testUser").password("1234").build();
        memberRepository.save(member);
        video = Video.builder().title("testTitle").viewCount(0L).build();
        videoRepository.save(video);
        comment = Comment.builder().video(video).author(member).content("test").build();
    }

    @Test
    @DisplayName("댓글이 DB에 잘 저장되는지 확인")
    void saveComment() {
        // given
        // static member, video, comment 사용

        // when
        Comment savedComment = commentRepository.save(comment);

        // then
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getAuthor()).isEqualTo(member);
        assertThat(savedComment.getVideo()).isEqualTo(video);
        assertThat(savedComment.getContent()).isEqualTo("test");
    }

    @Test
    @DisplayName("댓글 목록이 잘 로드되는지 확인")
    void loadComments() {
        // given
        // static member, video, comment 사용
        Comment comment2 = Comment.builder().video(video).author(member).content("test2").build();
        commentRepository.save(comment);
        commentRepository.save(comment2);

        // when
        List<Comment> comments = commentRepository.findByVideoIdOrderByCreatedAtAsc(1L);

        // then
        assertThat(comments.size()).isEqualTo(2);
        Comment findComment = comments.get(1);
        assertThat(findComment.getAuthor()).isEqualTo(member);
        assertThat(findComment.getVideo()).isEqualTo(video);
        assertThat(findComment.getContent()).isEqualTo("test2");
    }

    @Test
    @DisplayName("댓글이 잘 삭제되는지 확인")
    void deleteComment() {
        // given
        // static member, video, comment 사용
        commentRepository.save(comment);

        // when
        commentRepository.delete(comment);
        List<Comment> comments = commentRepository.findAll();

        // then
        assertThat(comments.size()).isEqualTo(0);
    }
}
