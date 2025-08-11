package io.github.hs96wings.streaming_server.visitlog.service;

import io.github.hs96wings.streaming_server.visitlog.domain.VisitLog;
import io.github.hs96wings.streaming_server.visitlog.repository.VisitLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class VisitLogService {
    private final VisitLogRepository visitLogRepository;

    public VisitLogService(VisitLogRepository visitLogRepository) {
        this.visitLogRepository = visitLogRepository;
    }

    public void saveVisitLog(String ip, String ua, String path, String referer) {
        VisitLog log = new VisitLog(
                ip,
                ua,
                LocalDateTime.now(),
                path,
                referer
        );
        visitLogRepository.save(log);
    }
}
