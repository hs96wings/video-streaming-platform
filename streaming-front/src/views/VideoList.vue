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

<script>
import axios from 'axios'
import VideoCard from '@/components/VideoCard.vue'

export default {
    name: 'VideoList',
    components: { VideoCard },
    props: {
        isLogin: {
            type: Boolean,
            default: false,
            required: true
        },
    },
    data() {
        return {
            videoList: [],
            searchKeyword: "",
        }
    },
    async created() {
        const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/list`);
        this.videoList = response.data;
    },
    methods: {
        goToVideo(id) {
            this.$router.push(`/video/${id}`);
        },
        async searchVideos() {
            try {
                if (this.searchKeyword.trim === "") {
                    const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/list`);
                    this.videoList = response.data;
                } else {
                    const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/search?title=${this.searchKeyword}`)
                    this.videoList = response.data;
                }
            } catch (err) {
                alert("검색 중 오류가 발생했습니다");
                console.error(err);
            }
        }
    }
}
</script>