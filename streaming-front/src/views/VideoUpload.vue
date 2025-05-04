<template>
    <v-container>
        <v-row justify="center">
            <v-col cols="12" sm="4" md="6">
                <v-card>
                    <v-card-title class="text-h5 text-center">영상 추가</v-card-title>
                    <v-card-text>
                        <v-form @submit.prevent="add">
                            <v-text-field
                                label="영상 제목"
                                v-model="title"
                                required>
                            </v-text-field>
                            <v-text-field
                                label="영상 설명"
                                v-model="describe"
                                required>
                            </v-text-field>
                            <v-btn type="submit" color="primary" block>추가</v-btn>
                        </v-form>
                    </v-card-text>
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
            title: "",
            videoId: "",
        }
    },
    methods: {
        async add() {
            const videoData = { title: this.title, videoId: this.videoId }
            await axios.post(`${process.env.VUE_APP_API_BASE_URL}/api/video/`, videoData)
            window.location.href = '/video/list'
        }
    }
}
</script>