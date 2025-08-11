package io.github.hs96wings.streaming_server.video.dto

import org.springframework.web.multipart.MultipartFile

data class VideoSaveReqDto (
    val title: String? = null,
    val description: String? = null,
    val file: MultipartFile
)
