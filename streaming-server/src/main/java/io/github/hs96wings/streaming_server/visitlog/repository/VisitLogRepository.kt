package io.github.hs96wings.streaming_server.visitlog.repository

import io.github.hs96wings.streaming_server.visitlog.domain.VisitLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface VisitLogRepository : JpaRepository<VisitLog, Long> {
    @Query(
        "SELECT DATE(v.accessedAt) AS date, COUNT(DISTINCT v.ipAddress) AS count " +
                "FROM VisitLog v GROUP BY DATE(v.accessedAt) ORDER BY date DESC"
    )
    fun countDailyUniqueVisitors(): List<Array<Any>>
}
