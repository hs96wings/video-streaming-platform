package io.github.hs96wings.streaming_server.common.sse.controller;

import io.github.hs96wings.streaming_server.common.sse.service.SseEmitterService;
import io.github.hs96wings.streaming_server.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
public class SseController {
    private final SseEmitterService sseEmitterService;
    private final MemberRepository memberRepository;

    public SseController(SseEmitterService sseEmitterService, MemberRepository memberRepository) {
        this.sseEmitterService = sseEmitterService;
        this.memberRepository = memberRepository;
    }

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Long memberId = memberRepository.findByUserid(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"))
                .getId();

        return sseEmitterService.connect(memberId);
    }
}
