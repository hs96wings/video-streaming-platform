package io.github.hs96wings.streaming_server.video.service;

import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.dto.VideoResDto;
import io.github.hs96wings.streaming_server.video.dto.VideoSaveReqDto;
import io.github.hs96wings.streaming_server.video.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VideoService {
    private final VideoRepository videoRepository;
    @Value("${app.video.dir}")
    private String uploadDir;
    private static final Logger log = LoggerFactory.getLogger(VideoService.class);

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public Video upload(VideoSaveReqDto videoSaveReqDto) {
        try {
            MultipartFile file = videoSaveReqDto.getFile();
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            Path savePath = Paths.get(uploadDir, fileName);

            log.debug(savePath.toString());
            log.debug(savePath.toString());

            Files.createDirectories(savePath.getParent());
            file.transferTo(savePath);

            Video video = new Video();
            video.setTitle(videoSaveReqDto.getTitle());
            video.setDescription(video.getDescription());
            video.setVideoPath(baseUrl + uploadDir.replace(".", "") + fileName);

            videoRepository.save(video);

            return video;
        } catch (IOException e) {
            throw new RuntimeException("영상 업로드 실패", e);
        }
    }

    public List<VideoResDto> getVideos() {
        List<Video> videoList = videoRepository.findAll();
        List<VideoResDto> dtos = new ArrayList<>();
        for (Video v : videoList) {
            VideoResDto dto = new VideoResDto();
            dto.setId(v.getId());
            dto.setTitle(v.getTitle());
            dto.setDescription(v.getDescription());
            dto.setVideoPath(v.getVideoPath());
            dto.setThumbnailPath(v.getThumbnailPath());
            dto.setUploadedAt(v.getUploadedAt());
            dto.setVideoStatus(v.getVideoStatus());
            dtos.add(dto);
        }

        return dtos;
    }

    public VideoResDto findById(Long id) {
        Video video =  videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 영상이 존재하지 않습니다"));
        return new VideoResDto(video);
    }
}
