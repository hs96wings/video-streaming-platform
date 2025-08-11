package io.github.hs96wings.streaming_server.visitlog.domain

import jakarta.persistence.*
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.time.LocalDateTime

@Entity
@Table(name = "visit_log")
open class VisitLog (
    var ipAddress: String,
    var userAgent: String,
    var accessedAt: LocalDateTime,
    var path: String,
    var referer: String?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    constructor() : this("", "", LocalDateTime.now(), "", null)
}
