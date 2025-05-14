<template>
    <v-app-bar app dark>
        <v-container>
            <v-row align="center">
                <v-col class="d-flex justify-start">
                    <v-btn :to="{path: '/list'}">영상 목록</v-btn>
                    <v-btn :to="{path: '/chat'}">채팅</v-btn>
                </v-col>
                <v-col text="text-center">
                    <v-btn :to="{path: '/'}">Home</v-btn>
                </v-col>
                <v-col class="d-flex justify-end">
                    <v-btn v-if="isAdmin" :to="{path: '/admin'}">관리자</v-btn>
                    <v-btn v-if="!isLogin" :to="{path: '/create'}">회원가입</v-btn>
                    <v-btn v-if="!isLogin" :to="{path: '/login'}">로그인</v-btn>
                    <v-btn v-if="isLogin" @click="doLogout">로그아웃</v-btn>
                </v-col>
            </v-row>
        </v-container>
    </v-app-bar>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth';
import { computed } from 'vue';
import { useRouter } from 'vue-router';

// 1. Pinia 스토어 불러오기
const auth = useAuthStore()
const router = useRouter()

// 2. computed로 로그인/관리자 여부 가져오기
const isLogin = computed(() => auth.isLogin)
const isAdmin = computed(() => auth.isAdmin)

// 3. 로그아웃 시 스토어 액션 호출
function doLogout() {
    auth.logout()
    router.push('/')
}
</script>