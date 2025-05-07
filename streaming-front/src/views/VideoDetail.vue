<template>
    <v-container>
        <v-row justify="center">
            <v-col cols="12" sm="4" md="6">
                <v-card>
                    <v-card-title class="text-h5 text-center">{{ title }}</v-card-title>
                    <v-card-text>
                        <video
                            ref="hlsPlayer"
                            controls
                            width="540"
                            height="960"
                            crossorigin="anonymous"></video>
                    </v-card-text>
                    <v-btn
                        href="/list"
                        target="_self"
                        rel="noopener">
                        돌아가기
                    </v-btn>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import axios from 'axios'
import Hls from 'hls.js'

export default {
    data() {
        return {
            id: this.$route.params.id,
            title: "",
            videoPath: "",
            description: "",
            uploadedAt: ""
        }
    },
    async created() {
        const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/${this.id}`)
        this.title = response.data.title;
        this.videoPath = response.data.videoPath;
        this.description = response.data.description;

        const video = this.$refs.hlsPlayer;
        // safari (iOS)에서는 네이티브 재생
        if (video.canPlayType('application/vnd.apple.mpegurl')) {
            video.src = this.videoPath;
        } else if (Hls.isSupported()) {
            // 그 외 브라우저에는 Hls.js로 붙여주기
            const hls = new Hls();
            hls.loadSource(this.videoPath);
            hls.attachMedia(video);
            hls.on(Hls.Events.MANIFEST_PARSED, () => {
                video.play();
            })
        }
    },
    methods: {
        hlsPlayer() {
            
        }
    }
}
</script>