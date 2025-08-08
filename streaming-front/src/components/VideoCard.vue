<template>
    <v-card class="mx-auto" max-width="344">
        <v-img
            :src="video.thumbnailPath"
            height="180"
            class="white--text align-end"
        ></v-img>
        <v-card-title>{{ video.title }}</v-card-title>
        <v-card-subtitle>{{ formattedDate }} · 조회수: {{ video.viewCount }}회</v-card-subtitle>
    </v-card>
</template>

<script setup lang="ts">
import { computed } from 'vue';

interface Video {
    title: string,
    viewCount: number,
    thumbnailPath: string,
    uploadedAt: string
}

interface Props {
    video: Video
}

const props = defineProps<Props>()

const formattedDate = computed(() => {
    const d = new Date(props.video.uploadedAt)
    const yy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const dd = String(d.getDate()).padStart(2, '0');
    const hh = String(d.getHours()).padStart(2, '0');
    const mi = String(d.getMinutes()).padStart(2, '0');
    return `${yy}.${mm}.${dd} ${hh}:${mi}`;
})
</script>