package io.github.hs96wings.streaming_server.video.service;

import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.dto.VideoSaveReqDto;
import io.github.hs96wings.streaming_server.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class VideoService {
    private final VideoRepository videoRepository;
    @Value("${app.upload.dir}")
    private String uploadDir;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public Video upload(VideoSaveReqDto videoSaveReqDto) {
        try {
            MultipartFile file = videoSaveReqDto.getFile();
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path savePath = Paths.get(uploadDir, fileName);

            Files.createDirectories(savePath.getParent());
            file.transferTo(savePath);

            Video video = new Video();
            video.setTitle(videoSaveReqDto.getTitle());
            video.setDescription(video.getDescription());
            video.setVideoPath(savePath.toString());

            videoRepository.save(video);

            return video;
        } catch (IOException e) {
            throw new RuntimeException("영상 업로드 실패", e);
        }
    }
}
