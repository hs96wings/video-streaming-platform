package io.github.hs96wings.streaming_server.video.integration;

import io.github.hs96wings.streaming_server.member.integration.MemberServiceIntegrationTest;
import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.dto.VideoResDto;
import io.github.hs96wings.streaming_server.video.dto.VideoSaveReqDto;
import io.github.hs96wings.streaming_server.video.repository.VideoRepository;
import io.github.hs96wings.streaming_server.video.service.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class VideoServiceIntegrationTest {
    @Autowired
    VideoService videoService;
    @Autowired
    VideoRepository videoRepository;

    private VideoSaveReqDto videoSaveReqDto;
    private static final Logger log = LoggerFactory.getLogger(MemberServiceIntegrationTest.class);

    @BeforeEach
    void setUp() {
        videoSaveReqDto = new VideoSaveReqDto("테스트 제목", "테스트 설명",
                new MockMultipartFile("file", "test.mp4", MediaType.APPLICATION_OCTET_STREAM_VALUE, "dummy content".getBytes()));
    }

    @Test
    @Transactional
    @DisplayName("DB에 영상을 저장한다")
    void uploadVideo_shouldPersist() {
        // given
        Video uploadedVideo = videoService.upload(videoSaveReqDto);

        // when
        // assertThat(uploadedVideo.getId()).isEqualTo(1L);
        // 트랜잭션은 롤백되지만 MySQL의 AUTO_INCREMENT 카운터가 롤백되지 않아 에러가 뜬다
        assertThat(uploadedVideo.getId()).isNotNull().isPositive();

        // and then: DB에 존재
        List<Video> videoList = videoRepository.findAll();
        assertThat(videoList).hasSize(1);
    }

    @Test
    @Transactional
    @DisplayName("DB에서 영상 리스트를 가져온다")
    void getVideoList_shouldReturnPersistedVideos() {
        // given: 2개의 Video 엔티티를 직접 저장
        Video v1 = Video.builder()
                .title("첫 번째 영상")
                .description("설명")
                .videoPath("/path/video1.mp4")
                .thumbnailPath("/thumb/video1.mp4")
                .build();
        videoRepository.save(v1);
        Video v2 = Video.builder()
                .title("두 번째 영상")
                .description("설명")
                .videoPath("/path/video2.mp4")
                .thumbnailPath("/thumb/video2.mp4")
                .build();
        videoRepository.save(v2);

        // when: service 를 통해 DTO 리스트 조회
        List<VideoResDto> dtos = videoService.getVideos();

        // then: 사이즈 검증 + ID, 제목 매핑 확인
        assertThat(dtos)
                .hasSize(2)
                .extracting(
                        VideoResDto::getId,
                        VideoResDto::getTitle
                )
                .containsExactlyInAnyOrder(
                        tuple(v1.getId(), "첫 번째 영상"),
                        tuple(v2.getId(), "두 번째 영상")
                );
    }
}
