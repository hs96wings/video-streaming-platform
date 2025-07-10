package io.github.hs96wings.streaming_server.chat.controller;

import io.github.hs96wings.streaming_server.chat.dto.ChatMessageDto;
import io.github.hs96wings.streaming_server.chat.service.ChatService;
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
    @Mock
    private ChatService chatService;

    @Test
    @DisplayName("전송한 메시지를 올바른 토픽에 전송")
    void sendMessage_shouldSendToCorrectTopic() {
        // given
        Long roomId = 1L;
        ChatMessageDto dto = new ChatMessageDto("user1", "야호");

        // when
        stompController.sendMessage(roomId, dto);

        // then
        verify(messagingTemplate).convertAndSend("/topic/1", dto);
    }
}
