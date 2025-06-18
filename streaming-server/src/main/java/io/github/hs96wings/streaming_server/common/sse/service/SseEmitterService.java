package io.github.hs96wings.streaming_server.common.sse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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

        return emitter;
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
