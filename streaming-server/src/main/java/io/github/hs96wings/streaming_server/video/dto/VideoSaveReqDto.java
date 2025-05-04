package io.github.hs96wings.streaming_server.video.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoSaveReqDto {
    private String title;
    private String description;
    private MultipartFile file;
}
