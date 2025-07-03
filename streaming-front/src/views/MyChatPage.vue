<template>
    <v-container>
        <v-row>
            <v-col>
                <v-card>
                    <v-card-title class="text-center text-h5">
                        내 채팅 목록
                    </v-card-title>
                    <v-card-text>
                        <v-table>
                            <thead>
                                <tr>
                                    <th>채팅방 이름</th>
                                    <th>읽지 않은 메시지</th>
                                    <th>액션</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="chat in chatList" :key="chat.roomId">
                                    <td>{{ chat.roomName }}</td>
                                    <td>{{ chat.unReadCount }}</td>
                                    <td>
                                        <v-btn color="primary" @click="enterChatRoom(chat.roomId)">
                                            입장
                                        </v-btn>
                                        <v-btn color="secondary" :disabled="chat.isGroupChat === 'N'" @click="leaveChatRoom(chat.roomId)">
                                            나가기
                                        </v-btn>
                                    </td>
                                </tr>
                            </tbody>
                        </v-table>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted, onUnmounted, watch } from 'vue'
import axios from 'axios'
import { useNotificationStore } from '@/stores/notificationStore'

const router = useRouter()
const chatList = ref([])
const notificationStore = useNotificationStore()

onMounted(async () => {
    const { data } = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/chat/my/rooms`);
    chatList.value = data

    // 컴포넌트 마운트 시 SSE 연결 시도
    notificationStore.connectSSE()
})

onUnmounted(() => {
    notificationStore.disconnectSSE()
})

// 배열의 변화를 감지
watch(() => notificationStore.notifications, (newNotification) => {
    // 새 알림이 도착하면 (배열에 추가될 때마다)
    if (newNotification.length > 0) {
        // 마지막으로 추가된 알림을 가져옴 (또는 모든 새 알림을 순회)
        const lastestNotification = newNotification[newNotification.length - 1]

        // chatList에서 해당 roomId를 가진 채팅방을 찾아 unReadCount 업데이트
        const targetChat = chatList.value.find(chat => chat.roomId === lastestNotification.roomId)
        if (targetChat) {
            targetChat.unReadCount = lastestNotification.unreadCount
        }

        // 알림을 처리했으면 Pinia 스토어의 notifications 배열에서 해당 알림 제거
        notificationStore.notifications.pop()
    }
}, { deep: true }) // 객체 내부 변화 감지를 위해 deep: true 설정

function enterChatRoom(roomId) {
    router.push(`/chat/${roomId}`)
}

async function leaveChatRoom(roomId) {
    await axios.delete(`${import.meta.env.VITE_API_BASE_URL}/api/chat/room/group/${roomId}/leave`)
    chatList.value = chatList.value.filter(c => c.roomId !== roomId)
}

</script>