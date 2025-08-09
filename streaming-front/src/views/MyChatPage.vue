<template>
  <v-container>
    <v-row>
      <v-col>
        <v-card>
          <v-card-title class="text-center text-h5"> 내 채팅 목록 </v-card-title>
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
                    <v-btn color="primary" @click="enterChatRoom(chat.roomId)"> 입장 </v-btn>
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

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { ref, onMounted, onUnmounted } from 'vue';
import api from '@/api/axios';
import { useNotificationStore } from '@/stores/notificationStore';
import type { ChatRoom } from '@/types/api';
import { useSnackbarStore } from '@/stores/snackbarStore';

const router = useRouter();
const chatList = ref<ChatRoom[]>([]);
const notificationStore = useNotificationStore();
const snackBar = useSnackbarStore();

onMounted(async (): Promise<void> => {
  try {
    const data = await api.get<ChatRoom[]>(`/api/chat/my/rooms`);
    chatList.value = data;
  } catch (err: unknown) {
    snackBar.showSnackbar('채팅방 목록 조회에 실패했습니다', 'error');
  }

  // connectSSE 호출 시, 메시지를 받았을 때 실행할 함수를 직접 전달
  notificationStore.connectSSE((notification) => {
    const targetChat = chatList.value.find((chat) => chat.roomId === notification.roomId);
    if (targetChat) {
      targetChat.unReadCount = notification.unreadCount;
    }
  });
});

onUnmounted(() => {
  notificationStore.disconnectSSE();
});

function enterChatRoom(roomId: number): void {
  router.push(`/chat/${roomId}`);
}

async function leaveChatRoom(roomId: number): Promise<void> {
  try {
    await api.delete(`/api/chat/room/group/${roomId}/leave`);
    chatList.value = chatList.value.filter((c) => c.roomId !== roomId);
  } catch (err: unknown) {
    snackBar.showSnackbar('채팅방 삭제에 실패했습니다', 'error');
  }
}
</script>
