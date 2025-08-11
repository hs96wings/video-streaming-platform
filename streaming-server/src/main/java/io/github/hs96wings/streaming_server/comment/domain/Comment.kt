package io.github.hs96wings.streaming_server.comment.domain

import io.github.hs96wings.streaming_server.common.domain.BaseEntity
import io.github.hs96wings.streaming_server.member.domain.Member
import io.github.hs96wings.streaming_server.video.domain.Video
import jakarta.persistence.*

@Entity
open class Comment @JvmOverloads constructor(
    @Lob
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    var video: Video,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var author: Member
): BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long? = null
}
