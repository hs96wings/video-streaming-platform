import { createRouter, createWebHashHistory } from "vue-router"
import MemberCreate from '@/views/MemberCreate.vue'
import MemberLogin from '@/views/MemberLogin.vue'
import HomePage from '@/views/HomePage.vue'
import VideoUpload from "@/views/VideoUpload.vue"
import VideoList from '@/views/VideoList.vue'
import VideoDetail from '@/views/VideoDetail.vue'

import { jwtDecode } from 'jwt-decode'

const routes = [
    { path: '/', name: 'HomePage', component: HomePage },
    { path: '/create', name: 'MemberCreate', component: MemberCreate },
    { path: '/login', name: 'MemberLogin', component: MemberLogin },
    { path: '/upload', name: 'VideoUpload', component: VideoUpload, meta: { requireAuth: true, requireAdmin: true }},
    { path: '/list', name: 'VideoList', component: VideoList },
    { path: '/video/:id', name: 'VideoDetail', component: VideoDetail }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
});

// 네비게이션 가드
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem("token")

    if (to.meta.requireAuth) {
        if (!token) {
            return next('/login') // 로그인이 되지 않은 경우
        }

        const decodeToken = jwtDecode(token);
        const isAdmin = decodeToken.role === 'ADMIN';

        // ADMIN 페이지 접근 제한
        if (to.meta.requireAdmin && !isAdmin) {
            return next('/') // ADMIN이 아니면 돌려보냄
        }
    }

    next(); // 나머지 요청에 대해선 통과
})

export default router;