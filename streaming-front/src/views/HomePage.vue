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

<script setup>
import { ref, onMounted } from 'vue'
import VideoCard from '@/components/VideoCard.vue'
import axios from 'axios'
import {useRouter} from 'vue-router'
import { useSnackbarStore } from '@/stores/snackbarStore'

const router = useRouter()

const popularVideos = ref([])
const latestVideos = ref([])

const isLoadingPopular = ref(true)
const isLoadingLatest = ref(true)

const snackbarStore = useSnackbarStore()

const loadPopularVideos = async () => {
    isLoadingPopular.value = true
    try {
        const { data } = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/popular`)
        popularVideos.value = data
    } catch (err) {
        console.error("🔥 인기 영상 로딩 실패", err)
        snackbarStore.showSnackbar("인기 영상 로딩에 실패했습니다", 'warning')
    } finally {
        isLoadingPopular.value = false
    }
}

const loadLatestVideos = async () => {
    isLoadingLatest.value = true
    try {
        const { data } = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/latest`)
        latestVideos.value = data
    } catch (err) {
        console.error("🆕 최신 영상 로딩 실패", err)
        snackbarStore.showSnackbar("최신 영상 로딩에 실패했습니다", "warning")
    } finally {
        isLoadingLatest.value = false
    }
}

onMounted(async() => {
    loadPopularVideos()
    loadLatestVideos()

    await axios.post(`${process.env.VUE_APP_API_BASE_URL}/api/log/visit`)
})

function goToVideo(id) {
    router.push(`/video/${id}`)
}
</script>