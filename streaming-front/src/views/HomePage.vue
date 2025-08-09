<template>
  <v-container>
    <h2 class="text-h5 my-4">🔥 인기 영상</h2>
    <v-row>
      <template v-if="isLoadingPopular">
        <v-cols cols="12" sm="6" md="4" v-for="n in 3" :key="n">
          <v-skeleton-loader type="image, card" />
        </v-cols>
      </template>
      <template v-else>
        <v-col cols="12" sm="6" md="4" v-for="video in popularVideos" :key="video.id" @click="goToVideo(video.id)">
          <VideoCard :video="video" />
        </v-col>
      </template>
    </v-row>

    <h2 class="text-h5 my-4">🆕 최신 업로드</h2>
    <v-row>
      <template v-if="isLoadingLatest">
        <v-cols cols="12" sm="6" md="4" v-for="n in 3" :key="n">
          <v-skeleton-loader type="image, card" />
        </v-cols>
      </template>
      <template v-else>
        <v-col cols="12" sm="6" md="4" v-for="video in latestVideos" :key="video.id" @click="goToVideo(video.id)">
          <VideoCard :video="video" />
        </v-col>
      </template>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted, type Ref } from 'vue';
import VideoCard from '@/components/VideoCard.vue';
import api from '@/api/axios';
import { useRouter } from 'vue-router';
import { useSnackbarStore } from '@/stores/snackbarStore';
import type { Video } from '@/types/api';

const router = useRouter();
const snackbarStore = useSnackbarStore();

const popularVideos = ref<Video[]>([]);
const latestVideos = ref<Video[]>([]);

const isLoadingPopular = ref<boolean>(true);
const isLoadingLatest = ref<boolean>(true);

// 재사용을 위한 범용 로딩 함수
const loadVideos = async (
  apiEndpoint: string,
  videoRef: Ref<Video[]>,
  loadingRef: Ref<boolean>,
  errorContext: string
) => {
  loadingRef.value = true;
  try {
    const data = await api.get<Video[]>(apiEndpoint);
    videoRef.value = data;
  } catch (err: unknown) {
    console.error(`🔥 ${errorContext} 로딩 실패`, err);
    snackbarStore.showSnackbar(`${errorContext} 로딩에 실패했습니다.`, 'warning');
  } finally {
    loadingRef.value = false;
  }
};

onMounted(async () => {
  await Promise.all([
    loadVideos('/api/video/popular', popularVideos, isLoadingPopular, '인기 영상'),
    loadVideos('/api/video/latest', latestVideos, isLoadingLatest, '최신 영상'),
  ]);

  await api.post(`/api/log/visit`).catch((err) => {
    console.error('방문 로그 기록 실패', err);
  });
});

function goToVideo(id: number) {
  router.push(`/video/${id}`);
}
</script>
