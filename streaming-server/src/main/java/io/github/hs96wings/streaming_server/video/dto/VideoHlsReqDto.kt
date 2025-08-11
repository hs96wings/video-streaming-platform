package io.github.hs96wings.streaming_server.video.dto

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

data class VideoHlsReqDto (
    val videoPath: String? = null,
    val thumbnailPath: String? = null
)
