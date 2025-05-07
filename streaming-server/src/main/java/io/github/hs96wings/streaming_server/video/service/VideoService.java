package io.github.hs96wings.streaming_server.video.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.domain.VideoStatus;
import io.github.hs96wings.streaming_server.video.dto.VideoHlsReqDto;
import io.github.hs96wings.streaming_server.video.dto.VideoModifyReqDto;
import io.github.hs96wings.streaming_server.video.dto.VideoResDto;
import io.github.hs96wings.streaming_server.video.dto.VideoSaveReqDto;
import io.github.hs96wings.streaming_server.video.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class VideoService {
    private final VideoRepository videoRepository;
    private final StringRedisTemplate redis;
    @Value("${app.video.dir}")
    private String uploadDir;
    private static final Logger log = LoggerFactory.getLogger(VideoService.class);

    public VideoService(VideoRepository videoRepository, StringRedisTemplate redis) {
        this.videoRepository = videoRepository;
        this.redis = redis;
    }

    public Video upload(VideoSaveReqDto videoSaveReqDto) {
        try {
            MultipartFile file = videoSaveReqDto.getFile();
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String resourcePath = uploadDir.startsWith("/")
                    ? uploadDir + "/"
                    : "/" + uploadDir + "/";
            Path uploadPath = Paths.get(uploadDir, fileName);
            String videoUrl = baseUrl + resourcePath + fileName;

            Files.createDirectories(uploadPath.getParent());
            file.transferTo(uploadPath);

            Video video = new Video();
            video.setTitle(videoSaveReqDto.getTitle());
            video.setDescription(video.getDescription());
            video.setVideoPath(videoUrl);

            videoRepository.save(video);

            // 작업 큐에 JSON 메시지 발행
            String job = new ObjectMapper().writeValueAsString(Map.of(
                    "videoId", video.getId(),
                    "path", video.getVideoPath()
            ));
            redis.opsForList().rightPush("videoQueue", job);

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
                .orElseThrow(() -> new IllegalArgumentException("해당 영상이 존재하지 않습니다"));
        return new VideoResDto(video);
    }

    @Transactional
    public Video modify(Long id, VideoModifyReqDto videoModifyReqDto) {
        Video modifyVideo = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 영상이 존재하지 않습니다"));

        modifyVideo.setTitle(videoModifyReqDto.getTitle());
        modifyVideo.setDescription(videoModifyReqDto.getDescription());

        return modifyVideo;
    }

    @Transactional
    public void delete(Long id) {
        Video deleteVideo = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 영상이 존재하지 않습니다"));

        videoRepository.delete(deleteVideo);
    }

    @Transactional
    public void updateStatus(Long id, VideoStatus status, VideoHlsReqDto videoHlsReqDto) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 영상이 존재하지 않습니다"));

        video.setVideoStatus(status);

        if (videoHlsReqDto.getVideoPath() != null)
            video.setVideoPath(videoHlsReqDto.getVideoPath());

        if (videoHlsReqDto.getThumbnailPath() != null)
            video.setThumbnailPath(videoHlsReqDto.getThumbnailPath());
    }
}
