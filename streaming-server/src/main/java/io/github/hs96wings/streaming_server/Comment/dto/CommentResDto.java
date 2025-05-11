package io.github.hs96wings.streaming_server.Comment.dto;

import io.github.hs96wings.streaming_server.Comment.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResDto {
    private Long id;
    private String authorName;
    private String content;
    private LocalDateTime createdAt;

    public CommentResDto(Comment comment) {
        this.id = comment.getId();
        this.authorName = comment.getAuthor().getUserid();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
    }
}
