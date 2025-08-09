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
import { formatDate } from '@/utils/date';

interface Props {
  video: Video;
}

const props = defineProps<Props>();

const formattedDate = computed(() => {
  return formatDate(props.video.uploadedAt);
});
</script>
