package io.github.hs96wings.streaming_server.video.controller;

import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.domain.VideoStatus;
import io.github.hs96wings.streaming_server.video.dto.VideoModifyReqDto;
import io.github.hs96wings.streaming_server.video.dto.VideoResDto;
import io.github.hs96wings.streaming_server.video.dto.VideoSaveReqDto;
import io.github.hs96wings.streaming_server.video.service.VideoService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/video")
public class VideoController {
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> videoUpload(@ModelAttribute VideoSaveReqDto videoSaveReqDto) {
        Video savedVideo = videoService.upload(videoSaveReqDto);

        return new ResponseEntity<>(savedVideo.getId(), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> videoList() {
        List<VideoResDto> videoResDtos = videoService.getVideos();

        return new ResponseEntity<>(videoResDtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVideo(@PathVariable("id") Long id) {
        VideoResDto video = videoService.findById(id);
        return new ResponseEntity<>(video, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifyVideo(@PathVariable("id") Long id, @RequestBody VideoModifyReqDto videoModifyReqDto) {
        Video modifyVideo = videoService.modify(id, videoModifyReqDto);

        return ResponseEntity.ok(new VideoResDto(modifyVideo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable("id") Long id) {
        videoService.delete(id);

        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> modifyVideoStatus(@PathVariable("id") Long id, @RequestParam("status") VideoStatus status) {
        videoService.updateStatus(id, status);

        return ResponseEntity.ok(id);
    }
}
