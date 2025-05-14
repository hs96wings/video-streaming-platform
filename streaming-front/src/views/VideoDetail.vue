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
                        @click="goToBack()"
                        target="_self"
                        rel="noopener">
                        돌아가기
                    </v-btn>
                </v-card>
                <v-card>
                    <v-textarea v-model="newComment" label="댓글을 입력하세요"></v-textarea>
                    <v-btn @click="postComment">등록</v-btn>
                </v-card>
                <v-card>
                    <v-table>
                        <thead>
                            <tr>
                                <th>작성자</th>
                                <th>내용</th>
                                <th>업로드 날짜</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="comment in comments" :key="comment.id">
                                <td>{{ comment.authorName }}</td>
                                <td>{{ comment.content }}</td>
                                <td>{{ formatDate(comment.createdAt) }}</td>
                                <td><v-btn v-if="comment.authorName === username" @click="deleteComment(comment.id)">삭제</v-btn></td>
                            </tr>
                        </tbody>
                    </v-table>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import Hls from 'hls.js'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const username = auth.username

const route = useRoute()
const router = useRouter()

const id = route.params.id
const title = ref('')
const videoPath = ref('')
const description = ref('')
const comments = ref([])
const newComment = ref('')
const hlsPlayer = ref(null)

function formatDate(datetime) {
    return datetime.slice(0, 19).replace('T', ' ')
}

async function fetchVideoAndComments() {
    const res = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/${id}`)
    const commentRes = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/comment/${id}`)
    title.value = res.data.title
    videoPath.value = res.data.videoPath
    description.value = res.data.description
    comments.value = commentRes.data
}

async function postComment() {
    await axios.post(`${process.env.VUE_APP_API_BASE_URL}/api/comment`, {
        videoId: id,
        content: newComment.value
    })
    newComment.value = ''
    fetchComments()
}

async function deleteComment(commentId) {
    await axios.delete(`${process.env.VUE_APP_API_BASE_URL}/api/comment/${commentId}`)
    fetchComments()
}

async function fetchComments() {
    const { data } = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/comment/${id}`)
    comments.value = data
}

function goToBack() {
    router.push('/list')
}

onMounted(async () => {
    await fetchVideoAndComments()

    const video = hlsPlayer.value
    if (video.canPlayType('application/vnd.apple.mpegurl')) {
        video.src = videoPath.value
    } else if (Hls.isSupported()) {
        const hls = new Hls()
        hls.loadSource(videoPath.value)
        hls.attachMedia(video)
        hls.on(Hls.Events.MANIFEST_PARSED, () => {
            video.play()
        })
    }
})
</script>