package io.github.hs96wings.streaming_server.visitlog.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "visit_log")
@Getter
@Setter
@NoArgsConstructor
public class VisitLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime accessedAt;
    private String path; // 어떤 경로로 접속했는지
    private String referer; // 외부 유입 경로

    @Builder
    public VisitLog(Long id, String ipAddress, String userAgent, LocalDateTime accessedAt, String path, String referer) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.accessedAt = accessedAt;
        this.path = path;
        this.referer = referer;
    }
}
