package io.github.hs96wings.streaming_server.visitlog.repository;

import io.github.hs96wings.streaming_server.visitlog.domain.VisitLog;
import io.github.hs96wings.streaming_server.admin.dto.VisitStatDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {
    @Query("SELECT DATE(v.accessedAt) AS date, COUNT(DISTINCT v.ipAddress) AS count " +
            "FROM VisitLog v GROUP BY DATE(v.accessedAt) ORDER BY date DESC")
    List<Object[]> countDailyUniqueVisitors();
}
