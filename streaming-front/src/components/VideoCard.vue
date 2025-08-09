<template>
  <v-card class="mx-auto" max-width="344">
    <v-img :src="video.thumbnailPath" height="180" class="white--text align-end"></v-img>
    <v-card-title>{{ video.title }}</v-card-title>
    <v-card-subtitle>{{ formattedDate }} · 조회수: {{ video.viewCount }}회</v-card-subtitle>
  </v-card>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { Video } from '@/types/api';

interface Props {
  video: Video;
}

const props = defineProps<Props>();

const formattedDate = computed(() => {
  const datetime = props.video.uploadedAt;

  if (!datetime) {
    return '날짜 정보 없음';
  }

  const date = new Date(datetime);

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
});
</script>
