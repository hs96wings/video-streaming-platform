package io.github.hs96wings.streaming_server.video.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hs96wings.streaming_server.member.integration.MemberServiceIntegrationTest;
import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.domain.VideoStatus;
import io.github.hs96wings.streaming_server.video.dto.VideoHlsReqDto;
import io.github.hs96wings.streaming_server.video.dto.VideoModifyReqDto;
import io.github.hs96wings.streaming_server.video.dto.VideoResDto;
import io.github.hs96wings.streaming_server.video.dto.VideoSaveReqDto;
import io.github.hs96wings.streaming_server.video.service.VideoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideoController.class)
@AutoConfigureMockMvc(addFilters = false) // 보안 필터 OFF
public class VideoControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private VideoService videoService;
    @Autowired
    private ObjectMapper objectMapper;

    private VideoSaveReqDto videoSaveReqDto;
    private VideoModifyReqDto videoModifyReqDto;
    private static final Logger log = LoggerFactory.getLogger(MemberServiceIntegrationTest.class);

    @BeforeEach
    void setUp() {
        videoSaveReqDto = new VideoSaveReqDto("테스트 제목", "테스트 설명",
                new MockMultipartFile("file", "test.mp4", MediaType.APPLICATION_OCTET_STREAM_VALUE, "dummy content".getBytes()));
        videoModifyReqDto = new VideoModifyReqDto("수정된 제목", "수정된 설명");
    }

    @Test
    @DisplayName("영상 업로드 요청 시 201 전달")
    void uploadVideo_returnsCreated() throws Exception {
        // given
        Video savedVideo = Video.builder()
                .id(1L)
                .title(videoSaveReqDto.getTitle())
                .description(videoSaveReqDto.getDescription())
                .videoPath("uploads/videos/test.mp4")
                .build();

        // upload가 호출되면 객체를 반환하도록 설정
        when(videoService.upload(any(VideoSaveReqDto.class))).thenReturn(savedVideo);

        // when & then: 최종적으로 1이라는 ID가 body로 나오는지 확인
        mockMvc.perform(multipart("/api/video/upload")
                        .file((MockMultipartFile) videoSaveReqDto.getFile())
                        .param("title", videoSaveReqDto.getTitle())
                        .param("description", videoSaveReqDto.getDescription()))
                        .andExpect(status().isCreated())
                        .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("영상 리스트 요청 시 200 전달")
    void getVideoList_returnsOkAndJson() throws Exception {
        // given: 더미 VideoResDto 객체를 직접 만든다
        VideoResDto dto = new VideoResDto();
        dto.setId(1L);
        dto.setTitle("테스트 영상");
        dto.setDescription("테스트 설명");
        dto.setVideoPath("/uploads/videos/test.mp4");
        dto.setThumbnailPath("/path/to/thumb.png");
        dto.setUploadedAt(LocalDateTime.of(2025, 5, 5, 12, 0));
        dto.setVideoStatus(VideoStatus.UPLOADED);

        List<VideoResDto> videoResDtos = List.of(dto);

        // videoService.getVideos()가 호출되면 dummyList를 반환하도록 세팅
        when(videoService.getVideos()).thenReturn(videoResDtos);

        // when & then
        mockMvc.perform(get("/api/video/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // 응답 JSON 배열 길이가 1인지
                .andExpect(jsonPath("$.length()").value(1))
                // 첫 번째 요소의 id, title이 기댓값인지
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("테스트 영상"))
                .andExpect(jsonPath("$[0].videoPath").value("/uploads/videos/test.mp4"));
    }

    @Test
    @DisplayName("영상 하나 요청 시 200 전달")
    void getVideo_returnsOkAndJson() throws Exception {
        // given
        Video savedVideo = Video.builder()
                .id(1L)
                .title(videoSaveReqDto.getTitle())
                .description(videoSaveReqDto.getDescription())
                .videoPath("/uploads/videos/test.mp4")
                .build();
        VideoResDto dto = new VideoResDto(savedVideo);

        when(videoService.findById(anyLong())).thenReturn(dto);

        // when & then
        mockMvc.perform(get("/api/video/{0}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("테스트 제목"))
                .andExpect(jsonPath("$.videoPath").value("/uploads/videos/test.mp4"));
    }

    @Test
    @DisplayName("없는 영상 요청 시 404 전달")
    void getVideo_returnsNotFound() throws Exception {
        // given: findById가 예외를 던지도록 설정
        when(videoService.findById(anyLong())).thenThrow(new EntityNotFoundException("존재하지 않는 영상입니다."));

        // when & then
        mockMvc.perform(get("/api/video/1000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("존재하지 않는 영상입니다.")));
    }

    @Test
    @DisplayName("영상 하나 수정 요청 시 200 전달")
    void modifyVideo_returnsOkAndJson() throws Exception {
        // given
        Video video = Video.builder()
                .id(1L)
                .title(videoModifyReqDto.getTitle())
                .description(videoModifyReqDto.getDescription())
                .videoPath("/uploads/videos/test.mp4")
                .build();

        when(videoService.modify(anyLong(), any(VideoModifyReqDto.class))).thenReturn(video);

        // when & then
        mockMvc.perform(patch("/api/video/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(videoModifyReqDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.description").value("수정된 설명"));
    }

    @Test
    @DisplayName("없는 영상 수정 요청 시 400 전달")
    void modifyVideo_returnsBadRequest() throws Exception {
        // given: modify가 예외를 던지도록 설정
        when(videoService.modify(anyLong(), any(VideoModifyReqDto.class))).thenThrow(new IllegalArgumentException("해당 영상이 존재하지 않습니다"));

        // when & then
        mockMvc.perform(patch("/api/video/1000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(videoModifyReqDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("해당 영상이 존재하지 않습니다")));
    }

    @Test
    @DisplayName("영상 하나 삭제 요청 시 200 전달")
    void deleteVideo_returnsOk() throws Exception {
        // given: delete()는 기본 동작 (아무 예외 없이 성공) 그대로

        // 불필요한 stub: 기본적으로 void 메서드는 아무 동작없이 넘어가도록 설정되어 있음
        // doNothing().when(videoService).delete(anyLong());

        // when & then
        mockMvc.perform(delete("/api/video/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        // and then: 서비스에 delete(1L)이 호출됐는지 검증
        verify(videoService).delete(1L);
    }

    @Test
    @DisplayName("없는 영상 삭제 요청 시 400 전달")
    void deleteNotFoundVideo_returnBadRequest() throws Exception {
        // given: delete가 예외를 던지도록 설정
        doThrow(new IllegalArgumentException("해당 영상이 존재하지 않습니다"))
                .when(videoService).delete(anyLong());

        // when & then
        mockMvc.perform(delete("/api/video/{id}", 1000L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("해당 영상이 존재하지 않습니다"));
    }

    @Test
    @DisplayName("VideoStatus 변경 요청 시 200 전달")
    void updateVideoStatus_returnOk() throws Exception {
        // given: updateStatus()는 기본 동작 (아무 예외 없이 성공) 그대로
        VideoHlsReqDto videoHlsReqDto = new VideoHlsReqDto();

        // 불필요한 stub: 기본적으로 void 메서드는 아무 동작없이 넘어가도록 설정되어 있음

        // when & then
        mockMvc.perform(patch("/api/video/{id}/status", 1L).param("status", "PROCESSING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        // and then: 서비스에 delete(1L)이 호출됐는지 검증
        verify(videoService).updateStatus(1L, VideoStatus.PROCESSING, videoHlsReqDto);
    }

    @Test
    @DisplayName("videoStatus 변경 시 잘못된 값을 요청하면 400 전달")
    void updateVideoStatus_returnBadRequest() throws Exception {
        // given: updateStatus()는 기본 동작 (아무 예외 없이 성공) 그대로

        // 불필요한 stub: 기본적으로 void 메서드는 아무 동작없이 넘어가도록 설정되어 있음

        // when & then
        mockMvc.perform(patch("/api/video/{id}/status", 1L).param("status", "INVALID")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
