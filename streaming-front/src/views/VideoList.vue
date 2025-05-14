<template>
    <v-container fluid>
        <!-- 검색 기능 -->
        <v-text-field
            v-model="searchKeyword"
            label="영상 제목 검색"
            @input="searchVideos"
            clearable
        >
        </v-text-field>
        <v-row dense>
            <v-col
                v-for="video in videoList"
                :key="video.id"
                cols="12" sm="5" md="4" lg="3"
                @click="goToVideo(video.id)">
                <VideoCard :video="video" />
            </v-col>
        </v-row>
    </v-container>
</template>

<script setup>
import axios from 'axios'
import VideoCard from '@/components/VideoCard.vue'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const videoList = ref([])
const searchKeyword = ref('')

onMounted(async () => {
    const { data } = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/list`);
    videoList.value = data
})

function goToVideo(id) {
    router.push(`/video/${id}`)
}

async function searchVideos() {
    try {
        if (searchKeyword.value.trim() === "") {
            const { data } = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/list`);
            videoList.value = data
        } else {
            const { data } = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/search?title=${searchKeyword.value}`)
            videoList.value = data
        }
    } catch (err) {
        alert("검색 중 오류가 발생했습니다")
        console.error(err)
    }
}
</script>