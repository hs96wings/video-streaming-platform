package io.github.hs96wings.streaming_server.visitlog.service;

import io.github.hs96wings.streaming_server.visitlog.domain.VisitLog;
import io.github.hs96wings.streaming_server.visitlog.repository.VisitLogRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VisitLogServiceTest {
    @Mock
    private VisitLogRepository visitLogRepository;
    @InjectMocks
    private VisitLogService visitLogService;

    @Test
    @DisplayName("visitLog 생성 성공")
    void createVisitLog_success() {
        // given
        when(visitLogRepository.save(any(VisitLog.class))).thenAnswer(InvocationOnMock -> InvocationOnMock.getArgument(0));

        // when
        visitLogService.saveVisitLog("1.1.1.1", "Mozilla", "/test", "http://example.com");

        // then
        ArgumentCaptor<VisitLog> captor = ArgumentCaptor.forClass(VisitLog.class);
        verify(visitLogRepository, times(1)).save(captor.capture());

        VisitLog saved = captor.getValue();
        assertThat(saved.getIpAddress()).isEqualTo("1.1.1.1");
        assertThat(saved.getUserAgent()).isEqualTo("Mozilla");
        assertThat(saved.getPath()).isEqualTo("/test");
        assertThat(saved.getReferer()).isEqualTo("http://example.com");
    }
}
