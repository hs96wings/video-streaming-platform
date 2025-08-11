package io.github.hs96wings.streaming_server.video.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.github.hs96wings.streaming_server.video.domain.Video
import io.github.hs96wings.streaming_server.video.domain.VideoStatus
import java.time.LocalDateTime

data class VideoAdminResDto (
    val id: Long,
    val title: String? = null,
    val description: String? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val uploadedAt: LocalDateTime,
    val videoStatus: VideoStatus,
    val viewCount: Long
) {
    constructor(video: Video): this (
        id = video.id ?: throw IllegalStateException("Video ID cannot be null for DTO conversion"),
        title = video.title,
        description = video.description,
        uploadedAt = video.uploadedAt,
        videoStatus = video.videoStatus,
        viewCount = video.viewCount,
    )
}