package io.github.hs96wings.streaming_server.chat.dto;

import io.github.hs96wings.streaming_server.chat.domain.ChatRoom;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class ChatRoomListResDtoTest {
    @BeforeEach
    void setup() {

    }

    @Test
    @DisplayName("ChatRoom -> ChatRoomListResDto 변환이 정확히 수행되어야 한다")
    void fromEntity_shouldReturnValidDto() {
        ChatRoom chatRoom = ChatRoom.builder()
                .id(1L)
                .name("testRoom")
                .build();

        ChatRoomListResDto dto = ChatRoomListResDto.from(chatRoom);

        assertThat(dto.getRoomId()).isEqualTo(1L);
        assertThat(dto.getRoomName()).isEqualTo("testRoom");
    }
}
