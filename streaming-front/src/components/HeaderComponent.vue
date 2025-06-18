<template>
    <v-app-bar app dark density="compact">
        <v-container>
            <v-row align="center" class="w-100">
                <v-col cols="auto" class="d-flex justify-start">
                    <div class="d-none d-md-flex align-center">
                        <v-btn :to="{path: '/list'}">영상 목록</v-btn>
                         <v-btn v-if="isLogin" :to="{path: '/groupchat/list'}" class="ml-2">그룹챗 목록</v-btn>
                    </div>
                    <v-app-bar-nav-icon class="d-md-none" @click="toggleDrawer"></v-app-bar-nav-icon>
                </v-col>
                <v-col class="text-center d-flex justify-center flex-grow-1">
                    <v-btn :to="{path: '/'}">Home</v-btn>
                </v-col>
                <v-col cols="auto" class="d-flex justify-end">
                    <div class="d-none d-md-flex align-center flex-wrap justify-end">
                        <v-btn v-if="isAdmin" :to="{path: '/admin'}" class="mb-1 ml-md-2">관리자</v-btn>
                        <v-btn v-if="isLogin" :to="{path: '/my/chat/page'}" class="mb-1 ml-md-2">내 채팅 목록</v-btn>
                        <v-btn v-if="!isLogin" :to="{path: '/create'}" class="mb-1 ml-md-2">회원가입</v-btn>
                        <v-btn v-if="!isLogin" :to="{path: '/login'}" class="mb-1 ml-md-2">로그인</v-btn>
                        <v-btn v-if="isLogin" @click="doLogout" class="mb-1 ml-md-2">로그아웃</v-btn>
                    </div>

                    <template v-if="!isLogin && isMobile">
                        <v-btn :to="{path: '/create'}" class="ml-2">회원가입</v-btn>
                    </template>
                    <template v-if="!isLogin && isMobile">
                        <v-btn :to="{path: '/login'}" class="ml-2">로그인</v-btn>
                    </template>
                    <template v-else-if="isLogin && isMobile">
                        <v-btn @click="doLogout" class="ml-2">로그아웃</v-btn>
                    </template>
                </v-col>
            </v-row>
        </v-container>
    </v-app-bar>

    <v-navigation-drawer v-model="drawer" temporary location="left">
        <v-list>
            <v-list-item :to="{path: '/list'}">
                <v-list-item-title>영상 목록</v-list-item-title>
            </v-list-item>
            <v-list-item v-if="isLogin" :to="{path: '/groupchat/list'}">
                <v-list-item-title>그룹챗 목록</v-list-item-title>
            </v-list-item>
            <v-list-item v-if="isAdmin" :to="{path: '/admin'}">
                <v-list-item-title>관리자</v-list-item-title>
            </v-list-item>
            <v-list-item v-if="isLogin" :to="{path: '/my/chat/page'}">
                <v-list-item-title>내 채팅 목록</v-list-item-title>
            </v-list-item>
        </v-list>
    </v-navigation-drawer>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth';
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useDisplay } from 'vuetify'

// 반응형 네비게이션 드로어 상태
const drawer = ref(false)
const toggleDrawer = () => {
    drawer.value = !drawer.value
}

// Vuetify의 디스플레이 훅을 사용하여 모바일 여부 감지
const display = useDisplay()
const isMobile = computed(() => display.mobile.value)

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