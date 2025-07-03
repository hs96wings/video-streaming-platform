package io.github.hs96wings.streaming_server.visitlog.controller;

import io.github.hs96wings.streaming_server.visitlog.service.VisitLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/log")
public class VisitLogController {
    private final VisitLogService visitLogService;

    public VisitLogController(VisitLogService visitLogService) {
        this.visitLogService = visitLogService;
    }

    @PostMapping("/visit")
    public ResponseEntity<Void> logVisit(HttpServletRequest request) {
        String ip = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .orElse(request.getRemoteAddr());
        String ua = request.getHeader("User-Agent");
        String path = request.getRequestURI();
        String referer = request.getHeader("Referer");

        visitLogService.saveVisitLog(ip, ua, path, referer);
        return ResponseEntity.ok().build();
    }
}
