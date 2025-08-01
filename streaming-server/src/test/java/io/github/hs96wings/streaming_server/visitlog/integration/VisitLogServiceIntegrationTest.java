package io.github.hs96wings.streaming_server.visitlog.integration;

import io.github.hs96wings.streaming_server.visitlog.domain.VisitLog;
import io.github.hs96wings.streaming_server.visitlog.repository.VisitLogRepository;
import io.github.hs96wings.streaming_server.visitlog.service.VisitLogService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class VisitLogServiceIntegrationTest {
    @Autowired
    private VisitLogRepository visitLogRepository;
    @Autowired
    private VisitLogService visitLogService;

    @Test
    @DisplayName("DB에 로그를 저장한다")
    void saveVisitLog_shouldPersist() {
        // given
        VisitLog visitLog = VisitLog.builder()
                .ipAddress("1.2.3.4")
                .userAgent("Chrome")
                .path("/test")
                .referer("http://example.com")
                .build();
        visitLogRepository.save(visitLog);
        Long savedId = visitLog.getId();

        // when
        VisitLog savedVisitLog = visitLogRepository.findById(savedId)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 로그입니다."));

        // then
        assertAll("VisitLog Entity 검증",
            () -> assertThat(savedVisitLog.getIpAddress()).isEqualTo(visitLog.getIpAddress()),
            () -> assertThat(savedVisitLog.getUserAgent()).isEqualTo(visitLog.getUserAgent()),
            () -> assertThat(savedVisitLog.getPath()).isEqualTo(visitLog.getPath()),
            () -> assertThat(savedVisitLog.getReferer()).isEqualTo(visitLog.getReferer())
        );
    }

    @Test
    @DisplayName("Service를 통해 VisitLog가 저장된다")
    void saveVisitLog_byService_shouldPersist() {
        // when
        visitLogService.saveVisitLog("5.6.7.8", "Firefox", "/home", "http://example2.com");

        // then
        List<VisitLog> all = visitLogRepository.findAll();
        assertThat(all).anyMatch(v ->
            v.getIpAddress().equals("5.6.7.8") &&
            v.getUserAgent().equals("Firefox") &&
            v.getPath().equals("/home") &&
            v.getReferer().equals("http://example2.com")
        );
    }

    @Test
    @DisplayName("통계 쿼리가 실제로 유니크 방문자를 정확히 집계한다")
    void countDailyUniqueVisitors_shouldReturnCorrectStats() {
        visitLogService.saveVisitLog("1.2.3.4", "Chrome", "/test", "http://example.com");
        visitLogService.saveVisitLog("1.2.3.4", "Chrome", "/test", "http://example.com"); // 중복 IP
        visitLogService.saveVisitLog("2.2.2.2", "Chrome", "/test", "http://example.com");
        visitLogService.saveVisitLog("3.3.3.3", "Safari", "/home", "http://example2.com");

        List<Object[]> stats = visitLogRepository.countDailyUniqueVisitors();

        Object[] today = stats.get(0);
        LocalDate date = ((java.sql.Date) today[0]).toLocalDate();
        Long uniqueCount = (Long) today[1];

        assertThat(uniqueCount).isEqualTo(3L);
    }
}
