package io.github.hs96wings.streaming_server.admin.integration;

import io.github.hs96wings.streaming_server.admin.dto.VisitStatDto;
import io.github.hs96wings.streaming_server.admin.service.AdminStatsService;
import io.github.hs96wings.streaming_server.visitlog.domain.VisitLog;
import io.github.hs96wings.streaming_server.visitlog.repository.VisitLogRepository;
import io.github.hs96wings.streaming_server.visitlog.service.VisitLogService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class AdminStatsIntegrationTest {
    @Autowired
    private VisitLogRepository visitLogRepository;
    @Autowired
    private VisitLogService visitLogService;
    @Autowired
    private AdminStatsService adminStatsService;

    @Test
    @DisplayName("visitLog 저장 후 일별 유니크 방문자 수가 정확히 조회되어야 한다")
    void getDailyStats_shouldReturnCorrectCounts() {
        // given
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime yesterday = today.minusDays(1);

        // 오늘 날짜 로그 (IP 2개 중복 포함)
        visitLogRepository.save(VisitLog.builder().ipAddress("1.1.1.1").accessedAt(today).build());
        visitLogRepository.save(VisitLog.builder().ipAddress("2.2.2.2").accessedAt(today).build());
        visitLogRepository.save(VisitLog.builder().ipAddress("1.1.1.1").accessedAt(today).build());

        // 어제 날짜 로그 (IP 1개)
        visitLogRepository.save(VisitLog.builder().ipAddress("3.3.3.3").accessedAt(yesterday).build());

        // when
        List<VisitStatDto> result = adminStatsService.getDailyStats();

        // then
        assertThat(result).hasSize(2);

        Map<LocalDate, Long> dateToCount = result.stream()
                .collect(Collectors.toMap(VisitStatDto::getDate, VisitStatDto::getUniqueVisitorCount));

        assertThat(dateToCount.get(today.toLocalDate())).isEqualTo(2L);
        assertThat(dateToCount.get(yesterday.toLocalDate())).isEqualTo(1L);
    }
}
