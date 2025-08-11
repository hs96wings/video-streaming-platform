package io.github.hs96wings.streaming_server.visitlog.service

import io.github.hs96wings.streaming_server.visitlog.domain.VisitLog
import io.github.hs96wings.streaming_server.visitlog.repository.VisitLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class VisitLogService(
    private val visitLogRepository: VisitLogRepository
) {
    fun saveVisitLog(ip: String, ua: String, path: String, referer: String?) {
        val log: VisitLog = VisitLog(
            ip,
            ua,
            LocalDateTime.now(),
            path,
            referer
        )
        visitLogRepository.save(log)
    }
}
