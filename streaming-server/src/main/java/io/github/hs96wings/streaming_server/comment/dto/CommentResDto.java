package io.github.hs96wings.streaming_server.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.hs96wings.streaming_server.comment.domain.Comment;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public CommentResDto(Comment comment) {
        this.id = comment.getId();
        this.authorName = comment.getAuthor().getUserid();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
    }
}
