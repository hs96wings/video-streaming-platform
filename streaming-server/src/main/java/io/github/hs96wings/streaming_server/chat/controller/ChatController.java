package io.github.hs96wings.streaming_server.chat.controller;

import io.github.hs96wings.streaming_server.chat.dto.ChatMessageDto;
import io.github.hs96wings.streaming_server.chat.dto.ChatRoomListResDto;
import io.github.hs96wings.streaming_server.chat.dto.MyChatListResDto;
import io.github.hs96wings.streaming_server.chat.service.ChatService;
import io.github.hs96wings.streaming_server.member.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@EnableWebMvc
@Slf4j
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // 그룹 채팅방 개설
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/room/group/create")
    public ResponseEntity<?> createGroupRoom(@AuthenticationPrincipal Member member, @RequestParam(name = "roomName") String roomName) {
        chatService.createGroupRoom(member, roomName);
        return ResponseEntity.ok().build();
    }

    // 그룹 채팅 목록 조회
    @GetMapping("/room/group/list")
    public ResponseEntity<?> getGroupChatList() {
        List<ChatRoomListResDto> chatRooms = chatService.getGroupChatList();
        return new ResponseEntity<>(chatRooms, HttpStatus.OK);
    }

    // 그룹 채팅방 참여
    @PostMapping("/room/group/{roomId}/join")
    public ResponseEntity<?> joinGroupChatRoom(@AuthenticationPrincipal Member member, @PathVariable(name = "roomId") Long roomId) {
        chatService.addParticipantToGroupChat(member, roomId);
        return ResponseEntity.ok().build();
    }

    // 이전 메시지 조회
    @GetMapping("/history/{roomId}")
    public ResponseEntity<?> getChatHistory(@AuthenticationPrincipal Member member, @PathVariable(name = "roomId") Long roomId) {
        List<ChatMessageDto> chatMessageDtos = chatService.getChatHistory(member, roomId);
        return new ResponseEntity<>(chatMessageDtos, HttpStatus.OK);
    }

    // 채팅 메시지 읽음 처리
    @PostMapping("/room/{roomId}/read")
    public ResponseEntity<?> messageRead(@AuthenticationPrincipal Member member, @PathVariable(name = "roomId") Long roomId) {
        chatService.messageRead(member, roomId);
        return ResponseEntity.ok().build();
    }

    // 내 채팅방 목록 조회: roomId, roomName, isGroupChat, 메시지 읽음 개수
    @GetMapping("/my/rooms")
    public ResponseEntity<?> getMyChatRooms(@AuthenticationPrincipal Member member) {
        List<MyChatListResDto> myChatListResDtos = chatService.getMyChatRooms(member);
        return new ResponseEntity<>(myChatListResDtos, HttpStatus.OK);
    }

    // 채팅방 나가기
    @DeleteMapping("/room/group/{roomId}/leave")
    public ResponseEntity<?> leaveGroupChat(@AuthenticationPrincipal Member member, @PathVariable(name = "roomId") Long roomId) {
        chatService.leaveGroupChat(member, roomId);
        return ResponseEntity.ok().build();
    }

    // 개인 채팅방 개설 or 기존 roomId return
    @PostMapping("/room/private/create")
    public ResponseEntity<?> getOrCreatePrivateRoom(@AuthenticationPrincipal Member member, @RequestParam(name = "otherMemberUserId") String otherMemberUserId) {
        Long roomId = chatService.getOrCreatePrivateRoom(member, otherMemberUserId);
        return new ResponseEntity<>(roomId, HttpStatus.OK);
    }
}
