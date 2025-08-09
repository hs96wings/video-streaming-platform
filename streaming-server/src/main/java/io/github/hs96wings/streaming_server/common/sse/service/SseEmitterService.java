package io.github.hs96wings.streaming_server.common.sse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SseEmitterService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SseEmitter connect(Long memberId) {
        SseEmitter emitter = new SseEmitter(60 * 1000L); // 3분 유효
        emitters.put(memberId, emitter);

        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));
        emitter.onError((e) -> emitters.remove(memberId));

        // 첫 연결 시 503 Service Unavailable 방지를 위한 더미 전송
        try {
            emitter.send(SseEmitter.event().id("").name("connect").data("connected!"));
        } catch(IOException e) {
            log.error("SSE initial connection error for memberId={}: {}", memberId, e.getMessage());
            emitters.remove(memberId);
        }

        return emitter;
    }

    // 주기적으로 하트비트를 보내는 스케줄러 추가
    @Scheduled(fixedRate = 45000) // 45초마다 실행
    public void sendHeartbeat() {
        emitters.forEach((memberId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().id("").name("heartbeat").data("ping"));
            } catch (IOException e) {
                emitters.remove(memberId);
                log.info("Heartbeat failed, removed emitter for memberId={}", memberId);
            }
        });
    }

    public void send(Long memberId, Long roomId, Long unreadCount) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                // JSON 객체를 생성하여 전송
                Map<String, Object> data = new HashMap<>();
                data.put("roomId", roomId);
                data.put("unreadCount", unreadCount);

                emitter.send(SseEmitter.event().name("unreadCount").data(objectMapper.writeValueAsString(data)));
                log.info("SSE 'unreadCount' 이벤트 전송 성공: memberId={}, roomId={}, unreadCount={}", memberId, roomId, unreadCount);
            } catch (IOException e) {
                emitters.remove(memberId);
                log.error("SSE 'unreadCount' 이벤트 전송 중 IOException 발생: memberId={}, message={}", memberId, e.getMessage());
            }
        } else {
            log.warn("Emitter를 찾을 수 없습니다. (이미 연결이 종료되었을 수 있음)");
        }
    }
}
