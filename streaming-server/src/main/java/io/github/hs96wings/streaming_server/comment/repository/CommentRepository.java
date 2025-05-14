package io.github.hs96wings.streaming_server.comment.repository;

import io.github.hs96wings.streaming_server.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByVideoIdOrderByCreatedAtAsc(Long videoId);
}
