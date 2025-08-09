<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" md="8">
        <v-card>
          <v-card-title class="text-center text-h5">채팅</v-card-title>
          <v-card-text>
            <div class="chat-box" ref="chatBox">
              <div
                v-for="(msg, index) in messages"
                :key="index"
                :class="['chat-message', msg.senderUserid === auth.username ? 'sent' : 'received']"
              >
                <strong>{{ msg.senderUserid }}</strong
                >: {{ msg.message }}
              </div>
            </div>
            <v-text-field v-model="newMessage" label="메시지 입력" @keyup.enter="sendMessage" />
            <v-btn color="primary" block @click="sendMessage">전송</v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, nextTick, onBeforeUnmount, onMounted } from 'vue';
import { useRoute, useRouter, type NavigationGuardNext, type RouteLocationNormalized } from 'vue-router';
import SockJS from 'sockjs-client';
import { Client, type IMessage } from '@stomp/stompjs';
import { useAuthStore } from '@/stores/auth';
import { onBeforeRouteLeave } from 'vue-router';
import { useSnackbarStore } from '@/stores/snackbarStore';
import api from '@/api/axios';
import axios from 'axios';

interface ChatMessage {
  senderUserid: string | null;
  message: string;
}

const auth = useAuthStore();
const route = useRoute();
const router = useRouter();
const snackBar = useSnackbarStore();

const token: string | null = auth.token;

const messages = ref<ChatMessage[]>([]);
const newMessage = ref<string>('');
const chatBox = ref<HTMLElement | null>(null);
const roomId = ref<string>(route.params.roomId as string);

// 1. STOMP 클라이언트 생성
const stompClient = new Client({
  // 2. SockJS를 통해 커넥션을 만듬
  connectHeaders: { Authorization: `Bearer ${token}` },
  webSocketFactory: () => new SockJS(`${import.meta.env.VITE_API_BASE_URL}/connect`),
  reconnectDelay: 5000, // 연결 끊겼을 때 재연결 시도
});

// 3. 연결 후 콜백
stompClient.onConnect = (): void => {
  // 구독
  stompClient.subscribe(
    `/topic/${roomId.value}`,
    (message: IMessage) => {
      const parseMessage: ChatMessage = JSON.parse(message.body);
      messages.value.push(parseMessage);
      scrollToBottom();
    },
    {
      Authorization: `Bearer ${token}`,
    }
  );
};

stompClient.activate();

onMounted(async (): Promise<void> => {
  try {
    const data = await api.get(`/api/chat/history/${roomId.value}`);
    messages.value = data;
  } catch (err: unknown) {
    if (axios.isAxiosError(err) && err.response) {
      if (err.response.status === 403) {
        snackBar.showSnackbar('로그인이 필요합니다', 'warning');
        router.replace('/login');
      } else {
        snackBar.showSnackbar('문제가 발생했습니다', 'error');
      }
    } else {
      snackBar.showSnackbar('알 수 없는 문제가 발생했습니다', 'error');
    }
  }
});

function sendMessage(): void {
  if (newMessage.value.trim() === '') return;

  const message: ChatMessage = {
    senderUserid: auth.username,
    message: newMessage.value.trim(),
  };
  stompClient.publish({
    destination: `/publish/${roomId.value}`,
    body: JSON.stringify(message),
  });
  newMessage.value = '';
}

function scrollToBottom(): void {
  nextTick(() => {
    if (chatBox.value) {
      chatBox.value.scrollTop = chatBox.value.scrollHeight;
    }
  });
}

onBeforeUnmount(async (): Promise<void> => {
  await LeaveRoom();
});

onBeforeRouteLeave(
  async (to: RouteLocationNormalized, from: RouteLocationNormalized, next: NavigationGuardNext): Promise<void> => {
    await LeaveRoom();
    next();
  }
);

async function LeaveRoom(): Promise<void> {
  if (auth.token) {
    try {
      await api.post(`/api/chat/room/${roomId.value}/read`);
    } catch (err: unknown) {
      console.warn('읽음 처리 실패 (onBeforeRouteLeave)', err);
    }
  }
  if (stompClient.active) {
    stompClient.deactivate();
  }
}
</script>

<style>
.chat-box {
  height: 300px;
  overflow-y: auto;
  border: 1px solid #ddd;
  margin-bottom: 10px;
}

.chat-message {
  padding: 8px 12px;
  border-radius: 10px;
  max-width: fit-content;
}

.sent {
  text-align: right;
  background-color: #d1e7dd;
  margin-left: auto;
}

.received {
  text-align: left;
  background-color: #f8d7da;
  margin-right: auto;
}
</style>
