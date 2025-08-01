package io.github.hs96wings.streaming_server.admin.service;

import io.github.hs96wings.streaming_server.admin.dto.VisitStatDto;
import io.github.hs96wings.streaming_server.visitlog.domain.VisitLog;
import io.github.hs96wings.streaming_server.visitlog.repository.VisitLogRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AdminStatsServiceTest {
    @Mock
    private VisitLogRepository visitLogRepository;

    @InjectMocks
    private AdminStatsService adminStatsService;

    @Test
    @DisplayName("getDailyStats()는 raw 데이터를 VisitStatDto 리스트로 변환해야 한다")
    void getDailyStats_shouldMapRawDataToDto() {
        // given
        LocalDate date = LocalDate.of(2024, 7, 28);
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);
        Object[] row = new Object[] { sqlDate, 5L };

        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(row);

        given(visitLogRepository.countDailyUniqueVisitors()).willReturn(mockResult);

        // when
        List<VisitStatDto> result = adminStatsService.getDailyStats();

        // then
        assertThat(result).hasSize(1);
        VisitStatDto dto = result.get(0);
        assertThat(dto.getDate()).isEqualTo(date);
        assertThat(dto.getUniqueVisitorCount()).isEqualTo(5L);
    }
}
