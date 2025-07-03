<template>
    <v-container>
        <v-row justify="center">
            <v-col cols="12" sm="4" md="6">
                <v-card>
                    <v-card-title class="text-h5 text-center">영상 수정</v-card-title>
                    <v-card-text>
                        <v-form @submit.prevent="update">
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
                            <v-btn type="submit" color="primary" block>수정</v-btn>
                        </v-form>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const id = route.params.id
const title = ref('')
const description = ref('')

async function update() {
    const videoData = { title: title.value, description: description.value }
    await axios.patch(`${process.env.VUE_APP_API_BASE_URL}/api/video/${id}`, videoData)
    router.push('/admin')
}

onMounted(async () => {
    const res = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/${id}`)
    title.value = res.data.title
    description.value = res.data.description
})
</script>