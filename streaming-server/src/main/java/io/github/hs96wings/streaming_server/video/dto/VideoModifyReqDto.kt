package io.github.hs96wings.streaming_server.video.dto


data class VideoModifyReqDto (
    val title: String,
    val description: String? = null
)
