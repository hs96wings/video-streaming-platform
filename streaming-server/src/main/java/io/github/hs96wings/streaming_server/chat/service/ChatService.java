package io.github.hs96wings.streaming_server.chat.service;

import io.github.hs96wings.streaming_server.chat.domain.ChatMessage;
import io.github.hs96wings.streaming_server.chat.domain.ChatParticipant;
import io.github.hs96wings.streaming_server.chat.domain.ChatRoom;
import io.github.hs96wings.streaming_server.chat.domain.ReadStatus;
import io.github.hs96wings.streaming_server.chat.dto.ChatMessageDto;
import io.github.hs96wings.streaming_server.chat.dto.ChatRoomListResDto;
import io.github.hs96wings.streaming_server.chat.dto.MyChatListResDto;
import io.github.hs96wings.streaming_server.chat.repository.ChatMessageRepository;
import io.github.hs96wings.streaming_server.chat.repository.ChatParticipantRepository;
import io.github.hs96wings.streaming_server.chat.repository.ChatRoomRepository;
import io.github.hs96wings.streaming_server.chat.repository.ReadStatusRepository;
import io.github.hs96wings.streaming_server.member.domain.Member;
import io.github.hs96wings.streaming_server.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MemberRepository memberRepository;

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    public ChatService(ChatRoomRepository chatRoomRepository, ChatParticipantRepository chatParticipantRepository, ChatMessageRepository chatMessageRepository, ReadStatusRepository readStatusRepository, MemberRepository memberRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.readStatusRepository = readStatusRepository;
        this.memberRepository = memberRepository;
    }

    public void saveMessage(Long roomId, ChatMessageDto chatMessageDto) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("방을 찾을 수 없습니다"));

        // 보낸 사람 조회
        Member sender = memberRepository.findByUserid(chatMessageDto.getSenderUserid())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

        // 메시지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)
                .message(chatMessageDto.getMessage())
                .build();
        chatMessageRepository.save(chatMessage);

        // 사용자 별로 읽음 여부 저장
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        
        for (ChatParticipant c : chatParticipants) {
            ReadStatus readStatus = ReadStatus.builder()
                    .chatRoom(chatRoom)
                    .chatParticipant(c)
                    .chatMessage(chatMessage)
                    .isRead(c.getMember().equals(sender))
                    .build();
            readStatusRepository.save(readStatus);
        }
    }

    public void createGroupRoom(String roomName) {
        Member member = memberRepository.findByUserid(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("member cannot be found"));

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(roomName)
                .isGroupChat("Y")
                .build();
        chatRoomRepository.save(chatRoom);

        // 채팅 참여자로 채팅 개설자 추가
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }

    public List<ChatRoomListResDto> getGroupChatList() {
        return chatRoomRepository.findByIsGroupChat("Y").stream()
                .map(ChatRoomListResDto::from)
                .collect(Collectors.toList());
    }

    public void addParticipantToGroupChat(Long roomId) {
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 채팅방입니다"));
        // 멤버 조회
        Member member = memberRepository.findByUserid(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다"));

        if (chatRoom.getIsGroupChat().equals("N"))
            throw new IllegalArgumentException("그룹 채팅이 아닙니다");

        // 이미 참여한 유저인지 검증
        Optional<ChatParticipant> participant = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member);
        if (!participant.isPresent()) {
            addParticipantToRoom(chatRoom, member);
        }
    }

    // ChatParticipant 객체 생성 후 저장
    public void addParticipantToRoom(ChatRoom chatRoom, Member member) {
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }

    public List<ChatMessageDto> getChatHistory(Long roomId) {
        // 해당 채팅방에 참여자가 아닐 경우 에러
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 채팅방입니다"));
        Member member = memberRepository.findByUserid(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다"));
        List<ChatParticipant> chatParticipants = chatRoom.getChatParticipants();
        boolean check = false;

        for (ChatParticipant c : chatParticipants) {
            if (c.getMember().equals(member))
                check = true;
        }

        if (!check) throw new IllegalArgumentException("본인이 속하지 않은 채팅방입니다");

        // 특정 roomId에 대한 message 조회
        return chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom).stream()
                        .map(ChatMessageDto::from)
                        .collect(Collectors.toList());
    }

    public boolean isRoomParticipant(String userId, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 채팅방입니다"));
        Member member = memberRepository.findByUserid(userId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다"));
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);

        for (ChatParticipant c : chatParticipants) {
            if (c.getMember().equals(member))
                return true;
        }
        return false;
    }

    public void messageRead(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 채팅방입니다"));
        Member member = memberRepository.findByUserid(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다"));
        ChatParticipant chatParticipant = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 참여자입니다"));

        List<ReadStatus> readStatuses = readStatusRepository.findByChatParticipant(chatParticipant);

        for (ReadStatus r : readStatuses) {
            r.updateIsRead(true);
        }
    }

    public List<MyChatListResDto> getMyChatRooms() {
        Member member = memberRepository.findByUserid(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다"));
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findAllByMember(member);
        List<MyChatListResDto> chatListResDtos = new ArrayList<>();

        for (ChatParticipant c : chatParticipants) {
            Long count = readStatusRepository.countByChatParticipantAndIsReadFalse(c);
            MyChatListResDto dto = MyChatListResDto.builder()
                    .roomId(c.getChatRoom().getId())
                    .roomName(c.getChatRoom().getName())
                    .isGroupChat(c.getChatRoom().getIsGroupChat())
                    .unReadCount(count)
                    .build();
            chatListResDtos.add(dto);
        }
        return chatListResDtos;
    }

    public void leaveGroupChat(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 채팅방입니다"));
        Member member = memberRepository.findByUserid(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다"));

        if(chatRoom.getIsGroupChat().equals("N")) {
            throw new IllegalArgumentException("단체 채팅방이 아닙니다");
        }

        ChatParticipant c = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 참여자입니다"));
        chatParticipantRepository.delete(c);

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        if (chatParticipants.isEmpty()) {
            chatRoomRepository.delete(chatRoom);
        }
    }

    public Long getOrCreatePrivateRoom(String otherMemberUserId) {
        Member member = memberRepository.findByUserid(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다"));
        Member otherMember = memberRepository.findByUserid(otherMemberUserId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다"));

        if (member.equals(otherMember))
            throw new IllegalArgumentException("자기 자신과 채팅할 수 없습니다");

        String roomKey = generateRoomKey(member.getId(), otherMember.getId());

        return chatRoomRepository.findByRoomKey(roomKey)
                .map(ChatRoom::getId)
                .orElseGet(() -> {
                    try {
                        ChatRoom newRoom = ChatRoom.builder()
                                .isGroupChat("N")
                                .name(member.getUserid() + "-" + otherMember.getUserid())
                                .roomKey(roomKey)
                                .build();
                        chatRoomRepository.save(newRoom);
                        addParticipantToRoom(newRoom, member);
                        addParticipantToRoom(newRoom, otherMember);

                        return newRoom.getId();
                    } catch (DataIntegrityViolationException e) {
                        // 동시에 생성된 경우
                        return chatRoomRepository.findByRoomKey(roomKey)
                                .map(ChatRoom::getId)
                                .orElseThrow(() -> new RuntimeException("동시성 문제로 채팅방 조회 실패"));
                    }
                });
    }

    private String generateRoomKey(Long myId, Long otherMemberId) {
        return (myId < otherMemberId) ? myId + "-" + otherMemberId : otherMemberId + "-" + myId;
    }
}
