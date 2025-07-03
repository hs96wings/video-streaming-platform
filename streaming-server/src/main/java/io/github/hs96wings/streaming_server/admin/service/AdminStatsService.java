package io.github.hs96wings.streaming_server.admin.service;

import io.github.hs96wings.streaming_server.admin.dto.VisitStatDto;
import io.github.hs96wings.streaming_server.visitlog.repository.VisitLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminStatsService {
    private final VisitLogRepository visitLogRepository;

    public AdminStatsService(VisitLogRepository visitLogRepository) {
        this.visitLogRepository = visitLogRepository;
    }

    public List<VisitStatDto> getDailyStats() {
        List<Object[]> raw = visitLogRepository.countDailyUniqueVisitors();

        return raw.stream()
                .map(row -> new VisitStatDto(
                        ((java.sql.Date) row[0]).toLocalDate(),
                        (Long) row[1]
                ))
                .collect(Collectors.toList());
    }

}
