package io.github.hs96wings.streaming_server.chat.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @MessageMapping("/{roomId}") // 클라이언트에서 특정 publish/roomId 형태로 메시지를 발행 시 MessageMapping 수신
    @SendTo("/topic/{roomId}") // 해당 roomId에 메시지를 발행하여 구독 중인 클라이언트에게 전송
    public String sendMessage(@DestinationVariable("roomId") Long roomId, String message) {
        log.debug(message);

        return message;
    }
}
