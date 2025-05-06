package io.github.hs96wings.streaming_server.video.dto;

import io.github.hs96wings.streaming_server.video.domain.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoModifyReqDto {
    private String title;
    private String description;
}
