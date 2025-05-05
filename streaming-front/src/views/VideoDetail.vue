<template>
    <v-container>
        <v-row justify="center">
            <v-col cols="12" sm="4" md="6">
                <v-card>
                    <v-card-title class="text-h5 text-center">{{ title }}</v-card-title>
                    <v-card-text>
                        <video :src="videoPath"></video>
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
    }
}
</script>