<template>
    <v-container>
        <v-row justify="center">
            <v-col cols="12" sm="4" md="6">
                <v-card>
                    <v-card-title class="text-h5 text-center">회원가입</v-card-title>
                    <v-card-text>
                        <v-form @submit.prevent="create">
                            <v-text-field
                                label="아이디"
                                v-model="userid"
                                required>
                            </v-text-field>
                            <v-text-field
                                label="비밀번호"
                                v-model="password"
                                type="password"
                                required>
                            </v-text-field>
                            <v-btn type="submit" color="primary" block>회원가입</v-btn>
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
import { useAuthStore } from '@/stores/auth'

const userid = ref('')
const password = ref('')
const router = useRouter()
const auth = useAuthStore()

async function create() {
    const createData = { userid: userid.value, password: password.value }
    const res = await axios.post(`${process.env.VUE_APP_API_BASE_URL}/member/create`, createData)
    auth.updateAuthState(res.data.token) // spring에서 member.getId()와 HttpStatus.CREATED만 보내주기에 token이 없다 수정 필요
    router.push('/login')
}
</script>