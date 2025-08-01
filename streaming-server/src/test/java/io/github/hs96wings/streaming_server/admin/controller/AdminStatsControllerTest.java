package io.github.hs96wings.streaming_server.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hs96wings.streaming_server.admin.dto.VisitStatDto;
import io.github.hs96wings.streaming_server.admin.service.AdminStatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminStatsController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminStatsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AdminStatsService adminStatsService;
    @Autowired
    private ObjectMapper objectMapper;

    private VisitStatDto visitStatDto;

    @BeforeEach
    void setup() {
        LocalDate date = LocalDate.now();
        visitStatDto = new VisitStatDto(date, 1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("일별 방문 통계 요청 시 200 전달")
    void getDailyStats_shouldReturnList() throws Exception {
        List<VisitStatDto> dtos = List.of(visitStatDto);
        when(adminStatsService.getDailyStats()).thenReturn(dtos);

        mockMvc.perform(get("/api/admin/stats/visits/daily"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value(visitStatDto.getDate().toString()))
                .andExpect(jsonPath("$[0].uniqueVisitorCount").value(1));
    }
}
