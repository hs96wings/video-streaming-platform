package io.github.hs96wings.streaming_server.chat.repository;

import io.github.hs96wings.streaming_server.chat.domain.ChatParticipant;
import io.github.hs96wings.streaming_server.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {
    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);
}
