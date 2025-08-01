package io.github.hs96wings.streaming_server.visitlog.repository;

import io.github.hs96wings.streaming_server.visitlog.domain.VisitLog;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class VisitLogRepositoryTest {
    @Autowired
    private VisitLogRepository visitLogRepository;

    private VisitLog visitLog;

    @BeforeEach
    void setup() {
        visitLog = VisitLog.builder().ipAddress("1.2.3.4").userAgent("Chrome").path("/test").referer("http://example.com").build();
    }

    @Test
    @DisplayName("visitLog가 DB에 잘 저장되는지 확인")
    void saveVisitLog() {
        // given
        // static visitLog 사용

        // when
        VisitLog savedVisitLog = visitLogRepository.save(visitLog);

        // then
        assertThat(savedVisitLog).isNotNull();
        assertThat(savedVisitLog.getIpAddress()).isEqualTo("1.2.3.4");
        assertThat(savedVisitLog.getUserAgent()).isEqualTo("Chrome");
        assertThat(savedVisitLog.getPath()).isEqualTo("/test");
        assertThat(savedVisitLog.getReferer()).isEqualTo("http://example.com");
    }

    @Test
    @DisplayName("countDailyUniqueVisitor() 쿼리 정상 동작 확인")
    void countDailyUniqueVisitors_works() {
        // given
        visitLogRepository.save(VisitLog.builder()
                .ipAddress("1.2.3.4")
                .userAgent("Chrome")
                .path("/test")
                .referer("http://example.com")
                .accessedAt(LocalDateTime.of(2025, 8, 2, 12, 0))
                .build());
        visitLogRepository.save(VisitLog.builder()
                .ipAddress("2.2.2.2")
                .userAgent("Safari")
                .path("/home")
                .referer("http://example2.com")
                .accessedAt(LocalDateTime.of(2025, 8, 2, 13, 0))
                .build());
        visitLogRepository.save(VisitLog.builder()
                .ipAddress("1.2.3.4")
                .userAgent("Chrome")
                .path("/test")
                .referer("http://example.com")
                .accessedAt(LocalDateTime.of(2025, 8, 3, 10, 0))
                .build());

        // when
        List<Object[]> result = visitLogRepository.countDailyUniqueVisitors();

        // then
        assertThat(result).hasSize(2);
        Object[] day1 = result.get(0);
        Object[] day2 = result.get(1);
        assertThat(day1[1]).isEqualTo(1L);
        assertThat(day2[1]).isEqualTo(2L);
    }
}
