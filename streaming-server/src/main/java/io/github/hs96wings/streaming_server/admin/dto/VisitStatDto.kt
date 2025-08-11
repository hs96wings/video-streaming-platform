package io.github.hs96wings.streaming_server.admin.dto

import java.time.LocalDate

data class VisitStatDto (
    val date: LocalDate? = null,
    val uniqueVisitorCount: Long? = null
)
