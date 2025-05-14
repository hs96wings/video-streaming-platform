package io.github.hs96wings.streaming_server.chat.controller;

import io.github.hs96wings.streaming_server.chat.dto.ChatMessageReqDto;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessageSendingOperations messageTemplate;

    public ChatController(SimpMessageSendingOperations messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable("roomId") Long roomId, ChatMessageReqDto chatMessageReqDto) {
        log.debug(chatMessageReqDto.getMessage());
        messageTemplate.convertAndSend("/topic/" + roomId, chatMessageReqDto);
    }
}
