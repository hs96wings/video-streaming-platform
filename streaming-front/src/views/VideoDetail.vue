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
                                <td>{{ comment.createdAt.slice(0,19).replace('T',' ') }}</td>
                                <td><v-btn v-if="comment.authorName === username" @click="deleteComment(comment.id)">삭제</v-btn></td>
                            </tr>
                        </tbody>
                    </v-table>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script>
import axios from 'axios'
import Hls from 'hls.js'

export default {
    props: {
        username: {
            type: String,
            default: '',
            required: true
        }
    },
    data() {
        return {
            id: this.$route.params.id,
            title: "",
            videoPath: "",
            description: "",
            uploadedAt: "",
            comments: [],
            newComment: ''
        }
    },
    async created() {
        const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/${this.id}`)
        const comment_res = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/comment/${this.id}`)
        this.title = response.data.title;
        this.videoPath = response.data.videoPath;
        this.description = response.data.description;
        this.comments = comment_res.data;

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
        async fetchComments() {
            const { data } = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/comment/${this.id}`)
            this.comments = data
        },
        goToBack() {
            this.$router.push("/list");
        },
        async deleteComment(id) {
            await axios.delete(`${process.env.VUE_APP_API_BASE_URL}/api/comment/${id}`)
            this.fetchComments()
        },
        async postComment() {
            await axios.post(`${process.env.VUE_APP_API_BASE_URL}/api/comment`,
                { videoId: this.$route.params.id, content: this.newComment }
            )
            this.newComment = ''
            this.fetchComments()
        }
    }
}
</script>