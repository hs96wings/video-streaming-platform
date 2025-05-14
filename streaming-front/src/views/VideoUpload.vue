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

<script setup>
import axios from 'axios'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const title = ref('')
const description = ref('')
const file = ref(null)

function onFileChange(e) {
    file.value = e.target.files[0]
}

async function upload() {
    const formData = new FormData()
    formData.append("title", title.value)
    formData.append("description", description.value)
    formData.append("file", file.value)

    try {
        await axios.post(`${process.env.VUE_APP_API_BASE_URL}/api/video/upload`, formData)
        router.push('/admin')
    } catch (err) {
        console.log(`업로드 실패: ${err.response.data} ${formData}`)
    }
}
</script>