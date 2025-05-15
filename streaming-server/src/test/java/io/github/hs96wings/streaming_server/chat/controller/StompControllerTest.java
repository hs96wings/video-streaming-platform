package io.github.hs96wings.streaming_server.chat.controller;

import io.github.hs96wings.streaming_server.chat.dto.ChatMessageReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class StompControllerTest {
    @InjectMocks
    private StompController stompController;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Test
    @DisplayName("전송한 메시지를 올바른 토픽에 전송")
    void sendMessage_shouldSendToCorrectTopic() {
        // given
        Long roomId = 1L;
        ChatMessageReqDto dto = new ChatMessageReqDto("user1", "야호");

        // when
        stompController.sendMessage(roomId, dto);

        // then
        verify(messagingTemplate).convertAndSend("/topic/1", dto);
    }
}
