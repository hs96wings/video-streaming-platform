package io.github.hs96wings.streaming_server.video.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.hs96wings.streaming_server.video.domain.Video
import io.github.hs96wings.streaming_server.video.domain.VideoStatus
import io.github.hs96wings.streaming_server.video.dto.*
import io.github.hs96wings.streaming_server.video.repository.VideoRepository
import jakarta.persistence.EntityNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.Map
import java.util.stream.Collectors

@Service
@Transactional(readOnly = true)
class VideoService(
    private val videoRepository: VideoRepository,
    private val redis: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) {
    @Value("\${app.upload.dir}")
    private lateinit var uploadDir: String

    companion object {
        private val log = LoggerFactory.getLogger(VideoService::class.java)
    }

    @Transactional
    fun upload(videoSaveReqDto: VideoSaveReqDto): Video {
        try {
            val file: MultipartFile = videoSaveReqDto.file
            val fileName: String = "${UUID.randomUUID().toString()}_${file.originalFilename}"
            val baseUrl: String = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()

            val resourcePath: String = if (uploadDir.startsWith("/")) uploadDir else "/$uploadDir"
            val localPath: Path = Paths.get(uploadDir, "videos", fileName)
            val videoUrl: String = "$baseUrl$resourcePath/videos/$fileName"

            Files.createDirectories(localPath.parent)
            file.transferTo(localPath)

            val video: Video = Video(
                title = videoSaveReqDto.title,
                description = videoSaveReqDto.description,
                videoPath = videoUrl,
                thumbnailPath = null
            )

            videoRepository.save(video)

            val job = objectMapper.writeValueAsString(mapOf(
                "videoId" to video.id,
                "path" to localPath.toString()
            ))
            redis.opsForList().leftPush("videoQueue", job)

            log.info("Uploaded video successfully: id={}, path={}", video.id, localPath)

            return video
        } catch (e: IOException) {
            throw RuntimeException("영상 업로드 실패", e)
        }
    }

    fun getVideos(): List<VideoResDto> = searchByTitle("")

    fun findById(id: Long): VideoResDto {
        val video = videoRepository.findById(id)
            .orElse(null) ?: throw IllegalArgumentException("해당 영상이 존재하지 않습니다. id=$id")
        return VideoResDto.from(video)
    }

    @Transactional
    fun modify(id: Long, videoModifyReqDto: VideoModifyReqDto): Video {
        val modifyVideo = videoRepository.findById(id)
            .orElse(null) ?: throw IllegalArgumentException("해당 영상이 존재하지 않습니다. id=$id")

        return modifyVideo.apply {
            title = videoModifyReqDto.title
            description = videoModifyReqDto.description
        }
    }

    @Transactional
    fun delete(id: Long) {
        if (!videoRepository.existsById(id)) {
            throw IllegalArgumentException("해당 영상이 존재하지 않습니다. id=$id")
        }
        videoRepository.deleteById(id)
    }

    @Transactional
    fun updateStatus(id: Long, status: VideoStatus, videoHlsReqDto: VideoHlsReqDto) {
        val video = videoRepository.findById(id)
            .orElse(null) ?: throw IllegalArgumentException("해당 영상이 존재하지 않습니다. id=$id")

        video.videoStatus = status

        videoHlsReqDto.let {
            it.videoPath?.let { path -> video.videoPath = path }
            it.thumbnailPath?.let { thumb -> video.thumbnailPath = thumb }
        }
    }

    fun searchByTitle(title: String?): List<VideoResDto> {
        return videoRepository
            .findByVideoStatusAndTitleContaining(VideoStatus.READY, title ?: "")
            .map(VideoResDto::from)
    }

    fun findAll(): List<VideoAdminResDto> {
        return videoRepository.findAll().map(VideoAdminResDto::from)
    }

    @Transactional
    fun increaseViewCount(videoId: Long) {
        val video = videoRepository.findById(videoId)
            .orElse(null) ?: throw IllegalArgumentException("해당 영상이 존재하지 않습니다. id=$videoId")

        video.viewCount++
    }

    fun getTopViewedVideos(): List<VideoResDto> {
        return videoRepository.findTop3ByOrderByViewCountDesc().map(VideoResDto::from)
    }

    fun getLatestVideos(): List<VideoResDto> {
        return videoRepository.findTop3ByOrderByUploadedAtDesc().map(VideoResDto::from)
    }
}