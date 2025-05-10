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

<script>
import axios from 'axios'

export default {
    data() {
        return {
            id: this.$route.params.id,
            title: "",
            description: "",
        }
    },
    async created() {
        const response = await axios.get(`${process.env.VUE_APP_API_BASE_URL}/api/video/${this.id}`)
        this.title = response.data.title;
        this.description = response.data.description;
    },
    methods: {
        async update() {
            const videoData = { title: this.title, description: this.description }
            await axios.patch(`${process.env.VUE_APP_API_BASE_URL}/api/video/${this.id}`, videoData)
            this.$router.push('/admin')
        }
    }
}
</script>