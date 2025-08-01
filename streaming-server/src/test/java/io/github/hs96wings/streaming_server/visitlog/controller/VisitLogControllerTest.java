package io.github.hs96wings.streaming_server.visitlog.controller;

import io.github.hs96wings.streaming_server.visitlog.service.VisitLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(VisitLogController.class)
@AutoConfigureMockMvc(addFilters = false)
public class VisitLogControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VisitLogService visitLogService;

    @Test
    @DisplayName("로그 전달 요청 시 200 전달")
    void uploadLog_shouldReturn200() throws Exception {
        doNothing().when(visitLogService).saveVisitLog(anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(post("/api/log/visit")
                .header("X-Forwarded-For", "1.2.3.4")
                .header("User-Agent", "Chrome")
                .header("Referer", "http://example.com"))
                .andExpect(status().isOk());

        verify(visitLogService, times(1))
                .saveVisitLog(eq("1.2.3.4"), eq("Chrome"), eq("/api/log/visit"), eq("http://example.com"));
    }
}
