<template>
  <v-container fluid>
    <!-- 검색 기능 -->
    <v-text-field v-model="searchKeyword" label="영상 제목 검색" @input="searchVideos" clearable> </v-text-field>
    <div v-if="isLoading" class="text-center pa-10">
      <v-progress-circular indeterminate color="primary" :size="100"></v-progress-circular>
      <p class="mt-4">영상을 불러오는 중입니다...</p>
    </div>
    <v-row dense v-else-if="videoList">
      <v-col v-for="video in videoList" :key="video.id" cols="12" sm="5" md="4" lg="3" @click="goToVideo(video.id)">
        <VideoCard :video="video" />
      </v-col>
    </v-row>
    <div v-else class="text-center pa-10">
      <p>영상을 불러오는 데 실패했습니다.</p>
    </div>
  </v-container>
</template>

<script setup lang="ts">
import api from '@/api/axios';
import VideoCard from '@/components/VideoCard.vue';
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import type { Video } from '@/types/api';

const router = useRouter();
const videoList = ref<Video[]>([]);
const searchKeyword = ref<string>('');
const isLoading = ref<boolean>(true);

onMounted(async (): Promise<void> => {
  isLoading.value = true;
  try {
    const data = await api.get(`/api/video/list`);
    videoList.value = data;
  } finally {
    isLoading.value = false;
  }
});

function goToVideo(id: number): void {
  router.push(`/video/${id}`);
}

async function searchVideos(): Promise<void> {
  try {
    if (searchKeyword.value.trim() === '') {
      const data = await api.get(`/api/video/list`);
      videoList.value = data;
    } else {
      const data = await api.get(`/api/video/search?title=${searchKeyword.value}`);
      videoList.value = data;
    }
  } catch (err: unknown) {
    alert('검색 중 오류가 발생했습니다');
    console.error(err);
  }
}
</script>
