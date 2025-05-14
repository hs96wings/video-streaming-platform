package io.github.hs96wings.streaming_server.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .setAllowedOrigins("http://localhost:3000", "http://localhost:8080", "http://43.200.192.246")
                // ws://가 아닌 http:// 엔드포인트를 사용할 수 있게 해주는 SockJS 라이브러리를 통한 요청을 허용하는 설정
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /publish/1 형태로 메시지 발행해야 함을 설정
        // /publish 로 시작하는 url 패턴으로 메시지가 발행되면 @Controller 객체의 @MessageMapping 메서드로 라우팅
        registry.setApplicationDestinationPrefixes("/publish");
        // /topic/1 형태로 메시지 수신(subscribe)해야 함을 설정
        registry.enableSimpleBroker("/topic");
    }
}
