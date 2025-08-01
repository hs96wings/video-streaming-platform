package io.github.hs96wings.streaming_server.chat.dto;

import io.github.hs96wings.streaming_server.chat.domain.ChatMessage;
import io.github.hs96wings.streaming_server.chat.domain.ChatRoom;
import io.github.hs96wings.streaming_server.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class ChatMessageDtoTest {
    private ChatRoom chatRoom;
    private Member member;

    @BeforeEach
    void setup() {
        chatRoom = ChatRoom.builder()
            .id(1L)
            .name("testRoom")
            .build();
        member = Member.builder()
            .id(1L)
            .userid("testUser")
            .build();
    }
    @Test
    @DisplayName("ChatMessage -> ChatMessageDto 변환이 정확히 수행되어야 한다")
    void fromEntity_shouldReturnValidDto() {
        ChatMessage chatMessage = ChatMessage.builder()
                .id(1L)
                .message("testMessage")
                .member(member)
                .chatRoom(chatRoom)
                .build();

        ChatMessageDto dto = ChatMessageDto.from(chatMessage);

        assertThat(dto.getMessage()).isEqualTo("testMessage");
        assertThat(dto.getSenderUserid()).isEqualTo("testUser");
    }
}
