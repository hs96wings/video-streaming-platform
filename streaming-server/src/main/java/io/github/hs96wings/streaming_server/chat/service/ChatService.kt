package io.github.hs96wings.streaming_server.chat.service

import io.github.hs96wings.streaming_server.chat.domain.ChatMessage
import io.github.hs96wings.streaming_server.chat.domain.ChatParticipant
import io.github.hs96wings.streaming_server.chat.domain.ChatRoom
import io.github.hs96wings.streaming_server.chat.domain.ReadStatus
import io.github.hs96wings.streaming_server.chat.dto.ChatMessageDto
import io.github.hs96wings.streaming_server.chat.dto.ChatRoomListResDto
import io.github.hs96wings.streaming_server.chat.dto.MyChatListResDto
import io.github.hs96wings.streaming_server.chat.repository.ChatMessageRepository
import io.github.hs96wings.streaming_server.chat.repository.ChatParticipantRepository
import io.github.hs96wings.streaming_server.chat.repository.ChatRoomRepository
import io.github.hs96wings.streaming_server.chat.repository.ReadStatusRepository
import io.github.hs96wings.streaming_server.common.sse.service.SseEmitterService
import io.github.hs96wings.streaming_server.member.domain.Member
import io.github.hs96wings.streaming_server.member.repository.MemberRepository
import jakarta.persistence.EntityNotFoundException
import lombok.extern.slf4j.Slf4j
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
@Slf4j
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val readStatusRepository: ReadStatusRepository,
    private val memberRepository: MemberRepository,
    private val sseEmitterService: SseEmitterService
) {
    fun saveMessage(roomId: Long, chatMessageDto: ChatMessageDto) {
        // 채팅방 조회
        val chatRoom = findByChatRoomId(roomId)
        val sender = findByUserId(chatMessageDto.senderUserid)

        // 메시지 저장
        val chatMessage = ChatMessage(
            chatRoom,
            sender,
            chatMessageDto.message
        )
        chatMessageRepository.save(chatMessage)

        // 사용자 별로 읽음 여부 저장
        val chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom)

        for (c in chatParticipants) {
            val isSender = c.member == sender

            val readStatus = ReadStatus(
                isSender,
                chatRoom,
                c,
                chatMessage
            )
            readStatusRepository.save(readStatus)

            if (!isSender) {
                val unreadCount = readStatusRepository.countByChatParticipantAndIsReadFalse(c)
                val targetRoomId = chatRoom.id
                sseEmitterService.send(c.member.id, targetRoomId, unreadCount)
            }
        }
    }

    fun createGroupRoom(member: Member, roomName: String) {
        // 채팅방 생성
        val chatRoom = ChatRoom(
            roomName,
            null,
            true
        )
        chatRoomRepository.save(chatRoom)

        // 채팅 참여자로 채팅 개설자 추가
        val chatParticipant = ChatParticipant(
            chatRoom,
            member
        )
        chatParticipantRepository.save(chatParticipant)
    }

    fun getGroupChatList(): List<ChatRoomListResDto> {
        return chatRoomRepository.findByIsGroupChat(true).map(ChatRoomListResDto::from)
    }

    fun addParticipantToGroupChat(member: Member, roomId: Long) {
        val chatRoom = findByChatRoomId(roomId)

        if (!chatRoom.isGroupChat) {
            throw IllegalArgumentException("그룹 채팅방이 아닙니다")
        }

        val participant = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member)
        if (!participant.isPresent) {
            addParticipantToRoom(chatRoom, member)
        }
    }

    fun addParticipantToRoom(chatRoom: ChatRoom, member: Member) {
        val chatParticipant: ChatParticipant = ChatParticipant(
            chatRoom,
            member
        )
        chatParticipantRepository.save(chatParticipant)
    }

    fun getChatHistory(member: Member, roomId: Long): List<ChatMessageDto> {
        val chatRoom = findByChatRoomId(roomId)
        val chatParticipants: List<ChatParticipant> = chatRoom.chatParticipants
        var check = false

        for (c in chatParticipants) {
            if (c.member.id == member.id) {
                check = true
                break
            }
        }

        if (!check) {
            throw IllegalArgumentException("본인이 속하지 않은 채팅방입니다")
        }

        return chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom)
            .map(ChatMessageDto::from)
    }

    fun isRoomParticipant(userid: String, roomId: Long): Boolean {
        val chatRoom = findByChatRoomId(roomId)
        val member = findByUserId(userid)
        val chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom)

        for (c in chatParticipants) {
            if (c.member == member) return true
        }
        return false
    }

    fun messageRead(member: Member, roomId: Long) {
        val chatRoom = findByChatRoomId(roomId)
        val chatParticipant = findByChatRoomAndMember(chatRoom, member)

        val readStatuses = readStatusRepository.findByChatParticipant(chatParticipant)

        for (r in readStatuses) {
            r.updateIsRead(true)
        }
    }

    fun getMyChatRooms(member: Member): List<MyChatListResDto> {
        val chatParticipants = chatParticipantRepository.findAllByMember(member)
        val chatListResDtos: MutableList<MyChatListResDto> = ArrayList()

        for (c in chatParticipants) {
            val count = readStatusRepository.countByChatParticipantAndIsReadFalse(c)
            val dto = MyChatListResDto(
                c.chatRoom.id!!,
                c.chatRoom.name,
                c.chatRoom.isGroupChat,
                count
            )
            chatListResDtos.add(dto)
        }
        return chatListResDtos
    }

    fun leaveGroupChat(member: Member, roomId: Long) {
        val chatRoom = findByChatRoomId(roomId)
        if (!chatRoom.isGroupChat) {
            throw IllegalArgumentException("그룹 채팅방이 아닙니다")
        }

        val c = findByChatRoomAndMember(chatRoom, member)
        chatParticipantRepository.delete(c)

        val chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom)
        if (chatParticipants.isEmpty()) {
            chatRoomRepository.delete(chatRoom)
        }
    }

    fun getOrCreatePrivateRoom(member: Member, otherMemberUserId: String): Long {
        val otherMember = findByUserId(otherMemberUserId)
        val myId = member.id
        val otherId = otherMember.id

        if (myId == null || otherId == null) {
            throw IllegalStateException("채팅방을 생성하려면 모든 사용자의 ID가 존재해야 합니다")
        }

        if (member == otherMember) {
            throw IllegalArgumentException("자기 자신과 채팅할 수 없습니다")
        }

        val roomKey = generateRoomKey(myId, otherId)

        return chatRoomRepository.findByRoomKey(roomKey)?.id ?: run {
            try {
                val roomName = "${member.username}-${otherMember.username}"
                val newRoom = ChatRoom(roomName, roomKey, false)

                val savedRoom = chatRoomRepository.save(newRoom)
                addParticipantToRoom(savedRoom, member)
                addParticipantToRoom(savedRoom, otherMember)

                savedRoom.id ?: throw IllegalStateException("저장된 채팅방의 ID가 없습니다")
            } catch (e: DataIntegrityViolationException) {
                
                chatRoomRepository.findByRoomKey(roomKey)?.id
                    ?: throw RuntimeException("동시성 문제로 채팅방 조회에 실패했습니다")
            }
        }
    }

    private fun generateRoomKey(myId: Long, otherMemberId: Long): String {
        return if (myId < otherMemberId) "$myId-$otherMemberId" else "$otherMemberId-$myId"
    }

    private fun findByChatRoomId(roomId: Long): ChatRoom {
        return chatRoomRepository.findById(roomId)
            .orElse(null) ?: throw EntityNotFoundException("존재하지 않는 채팅방입니다")
    }

    private fun findByUserId(userid: String): Member {
        return memberRepository.findByUserid(userid)
            .orElse(null) ?: throw EntityNotFoundException("존재하지 않는 유저입니다")
    }

    private fun findByChatRoomAndMember(chatRoom: ChatRoom, member: Member): ChatParticipant {
        return chatParticipantRepository.findByChatRoomAndMember(chatRoom, member)
            .orElse(null) ?: throw EntityNotFoundException("존재하지 않는 참여자입니다")
    }
}
