package io.github.hs96wings.streaming_server.video.repository

import io.github.hs96wings.streaming_server.video.domain.Video
import io.github.hs96wings.streaming_server.video.domain.VideoStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoRepository : JpaRepository<Video, Long> {
    fun findByVideoStatusAndTitleContaining(status: VideoStatus, title: String): List<Video>

    // 조회수 기준 내림차순 정렬 후 상위 3개 가져오기
    fun findTop3ByOrderByViewCountDesc(): List<Video>

    // 업로드 날짜 기준 내림차순 정렬 후 상위 3개 가져오기
    fun findTop3ByOrderByUploadedAtDesc(): List<Video>
}
