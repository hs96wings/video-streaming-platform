<template>
    <v-btn :to="{path: '/upload'}">영상 업로드</v-btn>
    <v-table>
        <thead>
            <tr>
                <th>ID</th>
                <th>제목</th>
                <th>내용</th>
                <th>업로드 날짜</th>
                <th>상태</th>
                <th colspan="2">관리 메뉴</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="video in videoList" :key="video.id">
                <td>{{ video.id }}</td>
                <td @click="goToVideo(video.id)">{{ video.title }}</td>
                <td>{{ video.description }}</td>
                <td>{{ video.uploadedAt.slice(0, 19).replace('T', ' ') }}</td>
                <td v-if="video.videoStatus == 'READY'" class="text-green-darken-2">● {{ video.videoStatus }}</td>
                <td v-else-if="video.videoStatus == 'PROCESSING'" class="text-yellow-darken-2">● {{ video.videoStatus }}</td>
                <td v-else class="text-red-darken-3">● {{ video.videoStatus }}</td>
                <td><v-btn :to="{path: `/update/${video.id}`}">수정</v-btn></td>
                <td><v-btn @click="deleteVideo(video.id)">삭제</v-btn></td>
            </tr>
        </tbody>
    </v-table>
</template>

<script>
import axios from 'axios'
export default {
    data() {
        return {
            videoList: [],
        }
    },
    async created() {
        const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/admin`);
        this.videoList = response.data;
    },
    methods: {
        goToVideo(id) {
            this.$router.push(`/video/${id}`);
        },
        async deleteVideo(id) {
            await axios.delete(`${process.env.VUE_APP_API_BASE_URL}/api/video/${id}`)
            this.videoList = this.videoList.filter(v => v.id !== id)
        }
    }
}
</script>