package io.github.hs96wings.streaming_server.video.domain

import io.github.hs96wings.streaming_server.comment.domain.Comment
import io.github.hs96wings.streaming_server.common.domain.BaseEntity
import jakarta.persistence.*
import lombok.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
open class Video (
    @Column(nullable = false)
    var title: String,
    @Lob
    var description: String?,
    var videoPath: String?,
    var thumbnailPath: String?
): BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long? = null
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    val uploadedAt: LocalDateTime = LocalDateTime.now()
    @Enumerated(EnumType.STRING)
    var videoStatus: VideoStatus = VideoStatus.UPLOADED
    @Column(nullable = false)
    var viewCount: Long = 0L

    @OneToMany(mappedBy = "video", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    val comments: MutableList<Comment> = mutableListOf()
}
