package io.github.hs96wings.streaming_server.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageReqDto {
    private String senderUserid;
    private String message;
}
