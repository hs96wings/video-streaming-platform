package io.github.hs96wings.streaming_server.chat.dto;

import io.github.hs96wings.streaming_server.chat.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    private String senderUserid;
    private String message;

    public static ChatMessageDto from(ChatMessage chatMessage) {
        return ChatMessageDto.builder()
                .message(chatMessage.getMessage())
                .senderUserid(chatMessage.getMember().getUserid())
                .build();
    }
}
