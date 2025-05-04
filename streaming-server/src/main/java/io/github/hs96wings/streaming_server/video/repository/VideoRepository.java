package io.github.hs96wings.streaming_server.video.repository;

import io.github.hs96wings.streaming_server.video.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
}
