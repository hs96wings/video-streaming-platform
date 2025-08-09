<template>
  <v-container>
    <v-row>
      <v-col>
        <v-card>
          <v-card-title class="text-center text-h5">
            채팅방 목록
            <div class="d-flex justify-end">
              <v-btn color="secondary" @click="showCreateRoomModal = true"> 채팅방 생성 </v-btn>
            </div>
          </v-card-title>
          <v-card-text>
            <v-table>
              <thead>
                <tr>
                  <th>No</th>
                  <th>제목</th>
                  <th>채팅</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="chat in chatList" :key="chat.id">
                  <td>{{ chat.roomId }}</td>
                  <td>{{ chat.roomName }}</td>
                  <td>
                    <v-btn color="primary" @click="joinChatRoom(chat.roomId)"> 참여하기 </v-btn>
                  </td>
                </tr>
              </tbody>
            </v-table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    <v-dialog v-model="showCreateRoomModal" max-width="500px">
      <v-card>
        <v-card-title class="text-h6"> 채팅방 생성 </v-card-title>
        <v-card-text>
          <v-text-field label="방제목" v-model="newRoomTitle" />
        </v-card-text>
        <v-card-actions>
          <v-btn color="grey" @click="showCreateRoomModal = false">취소</v-btn>
          <v-btn color="primary" @click="createChatRoom">생성</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { ref, onMounted } from 'vue';
import api from '@/api/axios';
import type { ChatRoom } from '@/types/api';
import { useSnackbarStore } from '@/stores/snackbarStore';

const router = useRouter();
const snackBar = useSnackbarStore();
const showCreateRoomModal = ref<boolean>(false);
const newRoomTitle = ref<string>('');
const chatList = ref<ChatRoom[]>([]);

onMounted(async (): Promise<void> => {
  await loadChatRoom();
});

async function joinChatRoom(roomId: number): Promise<void> {
  try {
    await api.post(`/api/chat/room/group/${roomId}/join`);
    router.push(`/chat/${roomId}`);
  } catch (err: unknown) {
    snackBar.showSnackbar(`${roomId}번 채팅방 참여에 실패했습니다`, 'error');
  }
}

async function createChatRoom(): Promise<void> {
  if (!newRoomTitle.value.trim()) {
    alert('채팅방 제목을 입력해주세요');
    return;
  }
  try {
    await api.post(`/api/chat/room/group/create?roomName=${newRoomTitle.value}`, null);
    showCreateRoomModal.value = false;
    newRoomTitle.value = ''; // 성공 시 입력값 초기화
    await loadChatRoom();
  } catch (err: unknown) {
    snackBar.showSnackbar('채팅방 생성에 실패했습니다', 'error');
  }
}

async function loadChatRoom(): Promise<void> {
  try {
    const data = await api.get(`/api/chat/room/group/list`);
    chatList.value = data;
  } catch (err: unknown) {
    console.error('채팅 목록을 불러오는 데 실패했습니다.', err);
  }
}
</script>
