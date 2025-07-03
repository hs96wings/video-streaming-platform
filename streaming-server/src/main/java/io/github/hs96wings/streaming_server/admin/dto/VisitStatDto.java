package io.github.hs96wings.streaming_server.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VisitStatDto {
    private LocalDate date;
    private Long uniqueVisitorCount;
}
