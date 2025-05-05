<template>
    <v-container fluid>
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
        }
    },
    async created() {
        const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/list`);
        this.videoList = response.data;
    },
    methods: {
        goToVideo(id) {
            window.location.href="/video/" + id;
        }
    }
}
</script>