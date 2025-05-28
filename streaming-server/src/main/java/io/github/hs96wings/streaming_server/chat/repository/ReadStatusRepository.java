package io.github.hs96wings.streaming_server.chat.repository;

import io.github.hs96wings.streaming_server.chat.domain.ChatParticipant;
import io.github.hs96wings.streaming_server.chat.domain.ChatRoom;
import io.github.hs96wings.streaming_server.chat.domain.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, Long> {
    List<ReadStatus> findByChatParticipant(ChatParticipant chatParticipant);
    Long countByChatParticipantAndIsReadFalse(ChatParticipant chatParticipant);
}
