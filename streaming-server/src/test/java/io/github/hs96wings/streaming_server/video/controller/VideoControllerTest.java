package io.github.hs96wings.streaming_server.video.controller;

import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.dto.VideoSaveReqDto;
import io.github.hs96wings.streaming_server.video.service.VideoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideoController.class)
@AutoConfigureMockMvc(addFilters = false) // 보안 필터 OFF
public class VideoControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private VideoService videoService;

    @Test
    @DisplayName("영상 업로드 요청 시 201 전달")
    void uploadVideo_returnsCreated() throws Exception {
        // given
        VideoSaveReqDto videoSaveReqDto = new VideoSaveReqDto("테스트 제목", "테스트 설명",
                new MockMultipartFile("file", "test.mp4", MediaType.APPLICATION_OCTET_STREAM_VALUE, "dummy content".getBytes()));

        Video savedVideo = Video.builder()
                .id(1L)
                .title(videoSaveReqDto.getTitle())
                .description(videoSaveReqDto.getDescription())
                .videoPath("uploads/videos/test.mp4")
                .build();

        // upload가 호출되면 객체를 반환하도록 설정
        when(videoService.upload(any(VideoSaveReqDto.class))).thenReturn(savedVideo);

        // when & then: 최종적으로 1이라는 ID가 body로 나오는지 확인
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/video/upload")
                        .file((MockMultipartFile) videoSaveReqDto.getFile())
                        .param("title", videoSaveReqDto.getTitle())
                        .param("description", videoSaveReqDto.getDescription()))
                        .andExpect(status().isCreated())
                        .andExpect(content().string("1"));
    }
}
