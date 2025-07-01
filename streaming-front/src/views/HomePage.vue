<template>
    <v-container>
        <h2 class="text-h5 my-4">🔥 인기 영상</h2>
        <v-row>
            <v-col cols="12" sm="6" md="4" v-for="video in popularVideos" :key="video.id" @click="goToVideo(video.id)">
                <VideoCard :video="video" />
            </v-col>
        </v-row>

        <h2 class="text-h5 my-4">🆕 최신 업로드</h2>
        <v-row>
            <v-col cols="12" sm="6" md="4" v-for="video in latestVideos" :key="video.id" @click="goToVideo(video.id)">
                <VideoCard :video="video" />
            </v-col>
        </v-row>
    </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import VideoCard from '@/components/VideoCard.vue'
import axios from 'axios'
import {useRouter} from 'vue-router'

const router = useRouter()

const popularVideos = ref([])
const latestVideos = ref([])

onMounted(async() => {
    const popularRes = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/popular`)
    popularVideos.value = popularRes.data

    const latestRes = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/latest`)
    latestVideos.value = latestRes.data
})

function goToVideo(id) {
    router.push(`/video/${id}`)
}
</script>