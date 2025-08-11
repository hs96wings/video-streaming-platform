package io.github.hs96wings.streaming_server.admin.service

import io.github.hs96wings.streaming_server.admin.dto.VisitStatDto
import io.github.hs96wings.streaming_server.visitlog.repository.VisitLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Date
import java.util.stream.Collectors

@Service
@Transactional(readOnly = true)
class AdminStatsService(
    private val visitLogRepository: VisitLogRepository
) {
    fun getDailyStats(): List<VisitStatDto> {
        return visitLogRepository.countDailyUniqueVisitors().map{ (date, count) ->
            VisitStatDto(
                (date as java.sql.Date).toLocalDate(),
                count as Long
            )
        }
    }
}
