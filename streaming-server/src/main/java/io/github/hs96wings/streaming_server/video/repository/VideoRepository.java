package io.github.hs96wings.streaming_server.video.repository;

import io.github.hs96wings.streaming_server.video.domain.Video;
import io.github.hs96wings.streaming_server.video.domain.VideoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByVideoStatusAndTitleContaining(VideoStatus status, String title);

    // 조회수 기준 내림차순 정렬 후 상위 3개 가져오기
    List<Video> findTop3ByOrderByViewCountDesc();

    // 업로드 날짜 기준 내림차순 정렬 후 상위 3개 가져오기
    List<Video> findTop3ByOrderByUploadedAtDesc();
}
