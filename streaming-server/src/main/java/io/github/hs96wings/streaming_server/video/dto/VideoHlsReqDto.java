package io.github.hs96wings.streaming_server.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoHlsReqDto {
    private String videoPath;
    private String thumbnailPath;
}
