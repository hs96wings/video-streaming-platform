package io.github.hs96wings.streaming_server.video.controller;

import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.domain.VideoStatus;
import io.github.hs96wings.streaming_server.video.dto.*;
import io.github.hs96wings.streaming_server.video.service.VideoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/video")
public class VideoController {
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    // ADMIN 권한만(POST /api/video/upload)
    @PreAuthorize("hasRole('ADMIN')")
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

    // 누구나 조회 가능(GET /api/video/{id})
    @GetMapping("/{id}")
    public ResponseEntity<?> getVideo(@PathVariable("id") Long id) {
        VideoResDto video = videoService.findById(id);
        return new ResponseEntity<>(video, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> modifyVideo(@PathVariable("id") Long id, @RequestBody VideoModifyReqDto videoModifyReqDto) {
        Video modifyVideo = videoService.modify(id, videoModifyReqDto);

        return ResponseEntity.ok(new VideoResDto(modifyVideo));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable("id") Long id) {
        videoService.delete(id);

        return ResponseEntity.ok(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> modifyVideoStatus(@PathVariable("id") Long id, @RequestParam("status") VideoStatus status, @RequestBody VideoHlsReqDto videoHlsReqDto) {
        videoService.updateStatus(id, status, videoHlsReqDto);

        return ResponseEntity.ok(id);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchVideo(@RequestParam(name="title", required = false) String title) {
        List<VideoResDto> videoResDtos = videoService.searchByTitle(title);

        return new ResponseEntity<>(videoResDtos, HttpStatus.OK);
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getAllVideosForAdmin() {
        List<VideoAdminResDto> videoAdminResDtos = videoService.findAll();

        return new ResponseEntity<>(videoAdminResDtos, HttpStatus.OK);
    }
}
