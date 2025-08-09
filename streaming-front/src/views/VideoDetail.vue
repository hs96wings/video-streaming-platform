<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" sm="4" md="6">
        <div v-if="isLoading" class="text-center pa-10">
          <v-progress-circular indeterminate color="primary" :size="70"></v-progress-circular>
          <p class="mt-4">영상을 불러오는 중입니다...</p>
        </div>
        <v-card v-else-if="videoData">
          <v-card-title class="text-h5 text-center">{{ videoData.title }}</v-card-title>
          <v-card-text>
            <video ref="hlsPlayer" controls width="540" height="960" crossorigin="anonymous"></video>
          </v-card-text>
          <v-btn @click="goToBack()" target="_self" rel="noopener"> 돌아가기 </v-btn>
        </v-card>
        <div v-else class="text-center pa-10">
          <p>영상을 불러오는 데 실패했습니다.</p>
          <v-btn @click="goToBack()">돌아가기</v-btn>
        </div>
        <v-card>
          <v-textarea v-model="newComment" label="댓글을 입력하세요"></v-textarea>
          <v-btn @click="postComment">등록</v-btn>
        </v-card>
        <v-card>
          <v-table>
            <thead>
              <tr>
                <th>작성자</th>
                <th>내용</th>
                <th>업로드 날짜</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="comment in comments" :key="comment.id">
                <td>
                  <span v-if="comment.authorName === username"> {{ comment.authorName }}</span>
                  <v-btn v-else @click="openPrivateChatModal(comment.authorName)">{{ comment.authorName }}</v-btn>
                </td>
                <td>{{ comment.content }}</td>
                <td>{{ formatDate(comment.createdAt) }}</td>
                <td><v-btn v-if="comment.authorName === username" @click="deleteComment(comment.id)">삭제</v-btn></td>
              </tr>
            </tbody>
          </v-table>
        </v-card>
      </v-col>
    </v-row>
    <v-dialog v-model="showCreatePrivateRoomModal" max-width="500px">
      <v-card>
        <v-card-title class="text-h6"> 1:1 채팅방 생성 </v-card-title>
        <v-card-text> 채팅방을 생성하시겠습니까? </v-card-text>
        <v-card-actions>
          <v-btn color="grey" @click="showCreatePrivateRoomModal = false">취소</v-btn>
          <v-btn color="primary" @click="createPrivateChatRoom">생성</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '@/api/axios';
import Hls from 'hls.js';
import { useAuthStore } from '@/stores/auth';
import { storeToRefs } from 'pinia';
import type { Video, Comment } from '@/types/api';

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();
const { username } = storeToRefs(auth);

const idParam = route.params.id;
const id = Array.isArray(idParam) ? idParam[0] : idParam;

const videoData = ref<Video | null>(null);
const comments = ref<Comment[]>([]);
const newComment = ref<string>('');
const hlsPlayer = ref<HTMLVideoElement | null>(null);
const isLoading = ref<boolean>(true);

const showCreatePrivateRoomModal = ref<boolean>(false);
const targetUserId = ref<string>('');

function formatDate(datetime: any): string {
  // 백엔드에서 LocalDateTime 같은 날짜 객체를 별도 설정 없이 JSON으로 변환 중
  // 입력값이 배열이 아니거나, 날짜 정보를 담기에 길이가 충분하지 않으면 에러 처리
  if (!Array.isArray(datetime) || datetime.length < 6) {
    console.error('올바른 날짜 형식이 아닙니다:', datetime);
    return '날짜 형식 오류';
  }

  const [year, month, day, hour, minute, second, nanoseconds] = datetime;

  const date = new Date(year, month - 1, day, hour, minute, second, Math.floor(nanoseconds / 1000000));

  if (isNaN(date.getTime())) {
    return '유효하지 않은 날짜';
  }

  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  const hh = String(date.getHours()).padStart(2, '0');
  const mi = String(date.getMinutes()).padStart(2, '0');
  const ss = String(date.getSeconds()).padStart(2, '0');

  return `${yyyy}.${mm}.${dd} ${hh}:${mi}:${ss}`;
}

async function fetchVideoAndComments() {
  if (!id) return;
  isLoading.value = true;

  try {
    const [videoRes, commentRes] = await Promise.all([
      api.get<Video>(`/api/video/${id}`),
      api.get<Comment[]>(`/api/comment/${id}`),
    ]);

    videoData.value = videoRes;
    comments.value = commentRes;
  } catch (error: unknown) {
    if (error instanceof Error) {
      console.error('Failed to fetch video and comments:', error);
    } else {
      console.error('알 수 없는 오류:', error);
    }
  } finally {
    isLoading.value = false;
  }
}

async function postComment() {
  if (!id || !newComment.value.trim()) return;

  await api.post(`/api/comment`, {
    videoId: Number(id),
    content: newComment.value,
  });

  newComment.value = '';
  await fetchComments();
}

async function deleteComment(commentId: number) {
  await api.delete(`/api/comment/${commentId}`);
  await fetchComments();
}

async function fetchComments() {
  if (!id) return;

  const data = await api.get(`/api/comment/${id}`);
  comments.value = data;
}

function goToBack() {
  router.push('/list');
}

onMounted(async () => {
  await fetchVideoAndComments();

  const video = hlsPlayer.value;
  if (!video || !videoData.value) return;

  const videoSrc = videoData.value.videoPath;
  if (Hls.isSupported()) {
    const hls = new Hls();
    hls.loadSource(videoSrc);
    hls.attachMedia(video);
    hls.on(Hls.Events.MANIFEST_PARSED, () => {
      // video.play();
    });
  } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
    video.src = videoSrc;
  }
});

function openPrivateChatModal(userId: string) {
  targetUserId.value = userId;
  showCreatePrivateRoomModal.value = true;
}

async function createPrivateChatRoom() {
  const data = await api.post(`/api/chat/room/private/create?otherMemberUserId=${targetUserId.value}`);
  router.push(`/chat/${data}`);
}
</script>
