package io.github.hs96wings.streaming_server.video.repository;

import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.domain.VideoStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class VideoRepositoryTest {
    @Autowired
    private VideoRepository videoRepository;

    private Video video;
    @BeforeEach
    void setup() {
        video = Video.builder().title("testTitle").description("content").viewCount(0L).videoStatus(VideoStatus.READY).build();
    }

    @Test
    @DisplayName("영상이 DB에 잘 저장되는지 확인")
    void saveVideo() {
        // given
        // static video 사용

        // when
        Video savedVideo = videoRepository.save(video);

        // then
        assertThat(savedVideo).isNotNull();
        assertThat(savedVideo.getTitle()).isEqualTo(video.getTitle());
        assertThat(savedVideo.getDescription()).isEqualTo(video.getDescription());
        assertThat(savedVideo.getVideoStatus()).isEqualTo(video.getVideoStatus());
        assertThat(savedVideo.getViewCount()).isEqualTo(video.getViewCount());
    }

    @Test
    @DisplayName("영상이 잘 수정되는지 확인")
    void updateVideo() {
        // given
        videoRepository.save(video);
        Video findVideo = videoRepository.findById(video.getId()).orElseThrow();
        String updateTitle = "Update Title";
        String updateDescription = "Update Content";
        findVideo.setTitle(updateTitle);
        findVideo.setDescription(updateDescription);

        // when
        Video savedVideo = videoRepository.save(findVideo);

        // then
        assertThat(savedVideo.getId()).isEqualTo(video.getId());
        assertThat(savedVideo).hasFieldOrPropertyWithValue("title", updateTitle);
        assertThat(savedVideo).hasFieldOrPropertyWithValue("description", updateDescription);
    }

    @Test
    @DisplayName("영상이 잘 삭제되는지 확인")
    void deleteVideo() {
        // given
        // static video 사용
        videoRepository.save(video);

        // when
        videoRepository.delete(video);
        List<Video> videos = videoRepository.findAll();

        // then
        assertThat(videos.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("조회수 상위 영상 가져오기")
    void getTopViewedVideos() {
        // given (총 4개를 저장)
        videoRepository.save(video); // viewCount = 0L
        Video video1 = Video.builder().title("testTitle").description("content").viewCount(3L).build();
        videoRepository.save(video1);
        Video video2 = Video.builder().title("testTitle").description("content").viewCount(7L).build();
        videoRepository.save(video2);
        Video video3 = Video.builder().title("testTitle").description("content").viewCount(5L).build();
        videoRepository.save(video3);

        // when (4개 중 3개를 조회)
        List<Video> videos = videoRepository.findTop3ByOrderByViewCountDesc();
        Video top1Video = videos.get(0);
        Video top2Video = videos.get(1);
        Video top3Video = videos.get(2);

        // then
        assertThat(videos.size()).isEqualTo(3);
        assertThat(top1Video.getViewCount()).isEqualTo(7L);
        assertThat(top2Video.getViewCount()).isEqualTo(5L);
        assertThat(top3Video.getViewCount()).isEqualTo(3L);
    }

    @Test
    @DisplayName("업로드 최신 영상 가져오기")
    void getLatestVideos() {
        // given (총 4개를 저장)
        videoRepository.save(video);
        Video video1 = Video.builder().title("testTitle1").description("content1").viewCount(0L).build();
        videoRepository.save(video1);
        Video video2 = Video.builder().title("testTitle2").description("content2").viewCount(0L).build();
        videoRepository.save(video2);
        Video video3 = Video.builder().title("testTitle3").description("content3").viewCount(0L).build();
        videoRepository.save(video3);

        // when (4개 중 3개를 조회)
        List<Video> videos = videoRepository.findTop3ByOrderByUploadedAtDesc();
        Video top1Video = videos.get(0);
        Video top2Video = videos.get(1);
        Video top3Video = videos.get(2);

        // then
        assertThat(videos.size()).isEqualTo(3);
        assertThat(top1Video.getTitle()).isEqualTo("testTitle3");
        assertThat(top2Video.getTitle()).isEqualTo("testTitle2");
        assertThat(top3Video.getTitle()).isEqualTo("testTitle1");
    }

    @Test
    @DisplayName("특정 타이틀을 포함한 영상 가져오기")
    void findByVideoStatusAndTitleContaining() {
        // given
        videoRepository.save(video);
        Video video1 = Video.builder().title("otherTitle").description("content").viewCount(0L).videoStatus(VideoStatus.READY).build();
        videoRepository.save(video1);
        String searchTitle = "stT"; // testTitle 의 일부 stT

        // when
        List<Video> videos = videoRepository.findByVideoStatusAndTitleContaining(VideoStatus.READY, searchTitle);
        Video findVideo = videos.get(0);

        // then
        assertThat(videos.size()).isEqualTo(1);
        assertThat(findVideo.getTitle()).isEqualTo("testTitle");
        assertThat(findVideo.getDescription()).isEqualTo(video.getDescription());
        assertThat(findVideo.getVideoStatus()).isEqualTo(VideoStatus.READY);
    }
}
