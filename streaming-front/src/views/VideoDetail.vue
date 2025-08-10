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
            <div
              v-if="isBuffering"
              style="
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.4);
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
              "
            >
              <v-progress-circular indeterminate color="primary" :size="70"></v-progress-circular>
              <p class="mt-4 white--text">버퍼링 중...</p>
            </div>
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
import Hls, { Events, type ErrorData } from 'hls.js';
import { useAuthStore } from '@/stores/auth';
import { storeToRefs } from 'pinia';
import type { Video, Comment } from '@/types/api';
import { formatDate } from '@/utils/date';
import { useSnackbarStore } from '@/stores/snackbarStore';
import { hlsDefaultConfig } from '@/config/hlsConfig';

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
const isBuffering = ref<boolean>(false);

const showCreatePrivateRoomModal = ref<boolean>(false);
const targetUserId = ref<string>('');

const snackBar = useSnackbarStore();

async function fetchVideoAndComments(): Promise<void> {
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

async function postComment(): Promise<void> {
  if (!id || !newComment.value.trim()) return;

  await api.post(`/api/comment`, {
    videoId: Number(id),
    content: newComment.value,
  });

  newComment.value = '';
  await fetchComments();
}

async function deleteComment(commentId: number): Promise<void> {
  await api.delete(`/api/comment/${commentId}`);
  await fetchComments();
}

async function fetchComments(): Promise<void> {
  if (!id) return;

  const data = await api.get(`/api/comment/${id}`);
  comments.value = data;
}

function goToBack(): void {
  router.push('/list');
}

function attachHls(video: HTMLVideoElement, src: string): void {
  if (Hls.isSupported()) {
    const hls = new Hls(hlsDefaultConfig);
    hls.loadSource(src);
    hls.attachMedia(video);

    hls.on(Hls.Events.MANIFEST_PARSED, (): void => {
      isBuffering.value = false;
    });
    hls.on(Hls.Events.BUFFER_CREATED, (): void => {
      isBuffering.value = true;
    });
    hls.on(Hls.Events.BUFFER_APPENDED, (): void => {
      isBuffering.value = false;
    });
    hls.on(Hls.Events.ERROR, (_event: Events.ERROR, data: ErrorData): void => {
      console.error('Hls.js error:', data);

      if (data.fatal) {
        switch (data.type) {
          case Hls.ErrorTypes.NETWORK_ERROR:
            console.error('최종 네트워크 에러, 플레이어 중단');
            snackBar.showSnackbar('네트워크 에러가 발생했습니다', 'error');
            hls.destroy();
            break;
          case Hls.ErrorTypes.MEDIA_ERROR:
            console.log('미디어 에러, 복구 시도');
            snackBar.showSnackbar('영상 에러로 복구를 시도합니다', 'warning');
            hls.recoverMediaError();
            break;
          default:
            console.error('치명적 에러, 플레이어 중단');
            snackBar.showSnackbar('플레이어에 문제가 생겨 중단합니다', 'error');
            hls.destroy();
            break;
        }
      }
    });
  } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
    video.src = src;
  }
}

onMounted(async (): Promise<void> => {
  await fetchVideoAndComments();

  const video = hlsPlayer.value;
  if (!video || !videoData.value) return;

  attachHls(video, videoData.value.videoPath);
});

function openPrivateChatModal(userId: string): void {
  targetUserId.value = userId;
  showCreatePrivateRoomModal.value = true;
}

async function createPrivateChatRoom(): Promise<void> {
  const data = await api.post(`/api/chat/room/private/create?otherMemberUserId=${targetUserId.value}`);
  router.push(`/chat/${data}`);
}
</script>
