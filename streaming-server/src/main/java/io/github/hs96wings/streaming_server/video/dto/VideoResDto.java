package io.github.hs96wings.streaming_server.video.dto;

import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.domain.VideoStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoResDto {
    private Long id;
    private String title;
    private String description;
    private String videoPath;
    private String thumbnailPath;
    private LocalDateTime uploadedAt;
    private VideoStatus videoStatus;

    public VideoResDto(Video video) {
        this.id = video.getId();
        this.title = video.getTitle();
        this.description = video.getDescription();
        this.videoPath = video.getVideoPath();
        this.thumbnailPath = video.getThumbnailPath();
        this.uploadedAt = video.getUploadedAt();
        this.videoStatus = video.getVideoStatus();
    }
}
