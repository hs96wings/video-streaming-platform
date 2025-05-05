<template>
    <v-container>
        <v-row justify="center">
            <v-col cols="12" sm="4" md="6">
                <v-card>
                    <v-card-title class="text-h5 text-center">영상 추가</v-card-title>
                    <v-card-text>
                        <v-form @submit.prevent="upload">
                            <v-text-field
                                label="영상 제목"
                                v-model="title"
                                required>
                            </v-text-field>
                            <v-text-field
                                label="영상 설명"
                                v-model="description"
                                required>
                            </v-text-field>
                            <v-file-input
                                label="영상 파일"
                                @change="onFileChange">

                            </v-file-input>
                            <v-btn type="submit" color="primary" block>업로드</v-btn>
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
            description: "",
            file: null
        }
    },
    methods: {
        onFileChange(e) {
            this.file = e.target.files[0]
        },
        async upload() {
            const formData = new FormData()
            formData.append("title", this.title)
            formData.append("description", this.description)
            formData.append("file", this.file)

            await axios.post(`${process.env.VUE_APP_API_BASE_URL}/api/video/upload`, formData)
            .then(() => window.location.href = "/list")
            .catch(err => console.log(`업로드 실패: ${err.response.data}`))
        }
    }
}
</script>