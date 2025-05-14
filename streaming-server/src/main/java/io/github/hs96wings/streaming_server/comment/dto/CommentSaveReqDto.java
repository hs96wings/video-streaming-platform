package io.github.hs96wings.streaming_server.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentSaveReqDto {
    private Long videoId;
    private String content;
}
