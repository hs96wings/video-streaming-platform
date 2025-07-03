package io.github.hs96wings.streaming_server.admin.controller;

import io.github.hs96wings.streaming_server.admin.service.AdminStatsService;
import io.github.hs96wings.streaming_server.admin.dto.VisitStatDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {
    private final AdminStatsService adminStatsService;

    public AdminStatsController(AdminStatsService adminStatsService) {
        this.adminStatsService = adminStatsService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/visits/daily")
    public ResponseEntity<List<VisitStatDto>> getDailyVisitsStats() {
        List<VisitStatDto> stats = adminStatsService.getDailyStats();

        return ResponseEntity.ok(stats);
    }
}
