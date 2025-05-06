package io.github.hs96wings.streaming_server.video.integration;

import io.github.hs96wings.streaming_server.member.integration.MemberServiceIntegrationTest;
import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.domain.VideoStatus;
import io.github.hs96wings.streaming_server.video.dto.VideoModifyReqDto;
import io.github.hs96wings.streaming_server.video.dto.VideoResDto;
import io.github.hs96wings.streaming_server.video.dto.VideoSaveReqDto;
import io.github.hs96wings.streaming_server.video.repository.VideoRepository;
import io.github.hs96wings.streaming_server.video.service.VideoService;
import org.junit.jupiter.api.Assertions;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    private VideoModifyReqDto videoModifyReqDto;

    private static final Logger log = LoggerFactory.getLogger(MemberServiceIntegrationTest.class);

    @BeforeEach
    void setUp() {
        videoSaveReqDto = new VideoSaveReqDto("테스트 제목", "테스트 설명",
                new MockMultipartFile("file", "test.mp4", MediaType.APPLICATION_OCTET_STREAM_VALUE, "dummy content".getBytes()));
        videoModifyReqDto = new VideoModifyReqDto("수정된 제목", "수정된 설명");
    }

    @Test
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

    @Test
    @DisplayName("DB에서 영상 하나를 저장하고 조회한다")
    void uploadAndFindVideo_persistsAndRetrievesVideo() {
        // given: @BeforeEach에 있습니다

        // when: 업로드 후 바로 반환된 엔티티
        Video uploaded = videoService.upload(videoSaveReqDto);

        // then: Repository 에서 제대로 조회되는지 검증
        Video found = videoRepository.findById(uploaded.getId())
                .orElseThrow(() -> new AssertionError("조회된 영상이 없습니다."));

        assertAll("Video Entity 검증",
                () -> assertThat(found.getId()).isEqualTo(uploaded.getId()),
                () -> assertThat(found.getTitle()).isEqualTo(uploaded.getTitle()),
                () -> assertThat(found.getDescription()).isEqualTo(uploaded.getDescription()),
                () -> assertThat(found.getVideoPath()).isEqualTo(uploaded.getVideoPath()),
                () -> assertThat(found.getThumbnailPath()).isEqualTo(uploaded.getThumbnailPath()),
                () -> assertThat(found.getUploadedAt()).isEqualTo(uploaded.getUploadedAt()),
                () -> assertThat(found.getVideoStatus()).isEqualTo(uploaded.getVideoStatus())
        );
    }

    @Test
    @DisplayName("없는 영상 요청 시 오류를 반환한다")
    void findNotFoundVideo_ReturnError() {
        assertThrows(IllegalArgumentException.class, () -> videoService.findById(1000L));
    }

    @Test
    @DisplayName("DB에 영상 하나를 저장하고 수정한다")
    void uploadAndModifyVideo_persistsAndRetrievesVideo() {
        // given
        Video uploaded = videoService.upload(videoSaveReqDto);

        // when
        videoService.modify(uploaded.getId(), videoModifyReqDto);

        // then
        Video found = videoRepository.findById(uploaded.getId())
                .orElseThrow(() -> new AssertionError("조회된 영상이 없습니다."));

        assertAll("Video Entity 검증",
                () -> assertThat(found.getTitle()).isEqualTo("수정된 제목"),
                () -> assertThat(found.getDescription()).isEqualTo("수정된 설명"),
                // 변경되지 않아야 될 필드들
                () -> assertThat(found.getId()).isEqualTo(uploaded.getId()),
                () -> assertThat(found.getVideoPath()).isEqualTo(uploaded.getVideoPath()),
                () -> assertThat(found.getThumbnailPath()).isEqualTo(uploaded.getThumbnailPath()),
                () -> assertThat(found.getUploadedAt()).isEqualTo(uploaded.getUploadedAt()),
                () -> assertThat(found.getVideoStatus()).isEqualTo(uploaded.getVideoStatus())
        );
    }

    @Test
    @DisplayName("DB에 없는 영상을 수정 요청 시 오류를 반환한다")
    void modifyNotFoundVideo_ReturnError() {
        assertThrows(IllegalArgumentException.class, () -> videoService.modify(1000L, videoModifyReqDto));
    }

    @Test
    @DisplayName("DB에 영상 하나를 저장하고 삭제한다")
    void uploadAndDeleteVideo_persistsAndRetrievesVideo() {
        // given
        Video uploaded = videoService.upload(videoSaveReqDto);

        // when
        videoService.delete(uploaded.getId());

        // then: repository.findById가 빈 Optional을 반환했는지 검증
        Optional<Video> deleted = videoRepository.findById(uploaded.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("DB에 없는 영상을 삭제 요청 시 오류를 반환한다")
    void deleteNotFoundVideo_ReturnError() {
        assertThrows(IllegalArgumentException.class, () -> videoService.delete(1000L));
    }

    @Test
    @DisplayName("DB에 영상 하나를 저장하고 VideoStatus 값을 변경한다")
    void uploadAndUpdateVideoStatus_persistsAndRetrievesVideo() {
        // given
        Video uploaded = videoService.upload(videoSaveReqDto);

        // when
        videoService.updateStatus(uploaded.getId(), VideoStatus.PROCESSING);

        // then
        Video found = videoRepository.findById(uploaded.getId())
                        .orElseThrow(() -> new AssertionError("조회된 영상이 없습니다"));
        assertThat(found.getVideoStatus()).isEqualTo(VideoStatus.PROCESSING);
    }
}
