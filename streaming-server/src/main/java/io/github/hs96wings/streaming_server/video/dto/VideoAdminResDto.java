package io.github.hs96wings.streaming_server.video.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.domain.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoAdminResDto {
    private Long id;
    private String title;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime uploadedAt;
    private VideoStatus videoStatus;
    private Long viewCount;

    public VideoAdminResDto(Video video) {
        this.id = video.getId();
        this.title = video.getTitle();
        this.description = video.getDescription();
        this.uploadedAt = video.getUploadedAt();
        this.videoStatus = video.getVideoStatus();
        this.viewCount = video.getViewCount();
    }
}