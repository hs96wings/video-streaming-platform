package io.github.hs96wings.streaming_server.common.sse.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

public class SseEmitterServiceTest {
    private SseEmitterService sseEmitterService;

    @BeforeEach
    void setup() {
        sseEmitterService = new SseEmitterService();
    }

    @Test
    @DisplayName("connect() 호출 시 emitter가 저장되고 반환되어야 한다")
    void connect_shouldStoreEmitter() {
        Long memberId = 1L;

        SseEmitter sseEmitter = sseEmitterService.connect(memberId);

        assertThat(sseEmitter).isNotNull();
    }

    @Test
    @DisplayName("send() 호출 시 emitter가 존재하면 이벤트가 전송되어야 한다")
    void send_shouldTransmitEvent_ifEmitterExists() throws Exception {
        Long memberId = 1L;
        Long roomId = 1L;
        Long unreadCount = 3L;

        SseEmitter emitter = sseEmitterService.connect(memberId);

        // 수신 확인을 위해 콜백을 등록
        AtomicBoolean completed = new AtomicBoolean(false);
        emitter.onCompletion(() -> completed.set(true));

        // 이벤트 전송
        sseEmitterService.send(memberId, roomId, unreadCount);

        // emitter가 제거되지 않았는지 확인
        assertThat(completed.get()).isFalse();
    }
}
