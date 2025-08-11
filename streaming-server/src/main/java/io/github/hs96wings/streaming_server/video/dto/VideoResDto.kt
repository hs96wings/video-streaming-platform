package io.github.hs96wings.streaming_server.video.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.github.hs96wings.streaming_server.video.domain.Video
import java.time.LocalDateTime

data class VideoResDto (
    val id: Long,
    val title: String? = null,
    val description: String? = null,
    val videoPath: String? = null,
    val thumbnailPath: String? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val uploadedAt: LocalDateTime,
    val viewCount: Long? = null
) {
    constructor(video: Video): this (
        id = video.id ?: throw IllegalStateException("Video ID cannot be null for DTO conversion"),
        title = video.title,
        description = video.description,
        videoPath = video.videoPath,
        thumbnailPath = video.thumbnailPath,
        uploadedAt = video.uploadedAt,
        viewCount = video.viewCount
    )
}