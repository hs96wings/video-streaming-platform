package io.github.hs96wings.streaming_server.chat.dto;

import io.github.hs96wings.streaming_server.chat.domain.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomListResDto {
    private Long roomId;
    private String roomName;

    public static ChatRoomListResDto from(ChatRoom chatRoom) {
        return ChatRoomListResDto.builder()
                .roomId(chatRoom.getId())
                .roomName(chatRoom.getName())
                .build();
    }
}
