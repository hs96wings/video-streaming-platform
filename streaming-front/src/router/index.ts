import { createRouter, createWebHashHistory } from 'vue-router';
import MemberCreate from '@/views/MemberCreate.vue';
import MemberLogin from '@/views/MemberLogin.vue';
import HomePage from '@/views/HomePage.vue';
import VideoUpload from '@/views/VideoUpload.vue';
import VideoList from '@/views/VideoList.vue';
import VideoDetail from '@/views/VideoDetail.vue';
import AdminLayout from '@/views/AdminLayout.vue';
import AdminVideos from '@/views/AdminVideos.vue';
import AdminUpdateVideo from '@/views/AdminUpdateVideo.vue';
import AdminStats from '@/views/AdminStats.vue';
import ChatPage from '@/views/ChatPage.vue';
import GroupChatList from '@/views/GroupChatList.vue';
import MyChatPage from '@/views/MyChatPage.vue';

import { useAuthStore } from '@/stores/auth';
import { jwtDecode } from 'jwt-decode';

const routes = [
  { path: '/', name: 'HomePage', component: HomePage },
  { path: '/create', name: 'MemberCreate', component: MemberCreate },
  { path: '/login', name: 'MemberLogin', component: MemberLogin },
  { path: '/upload', name: 'VideoUpload', component: VideoUpload, meta: { requireAuth: true, requireAdmin: true } },
  { path: '/list', name: 'VideoList', component: VideoList },
  { path: '/video/:id', name: 'VideoDetail', component: VideoDetail },
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/videos',
    children: [
      {
        path: 'videos',
        name: 'AdminVideos',
        component: AdminVideos,
      },
      {
        path: 'stats',
        name: 'AdminStats',
        component: AdminStats,
      },
    ],
    meta: { requireAuth: true, requireAdmin: true },
  },
  {
    path: '/update/:id',
    name: 'AdminUpdateVideo',
    component: AdminUpdateVideo,
    meta: { requireAuth: true, requireAdmin: true },
  },
  { path: '/chat/:roomId', name: 'ChatPage', component: ChatPage },
  { path: '/groupchat/list', name: 'GroupChatList', component: GroupChatList, meta: { requireAuth: true } },
  { path: '/my/chat/page', name: 'MyChatPage', component: MyChatPage, meta: { requireAuth: true } },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

interface DecodedToken {
  sub: string;
  role: string;
}

// 네비게이션 가드
router.beforeEach((to, from, next) => {
  const auth = useAuthStore();
  const token = auth.token;

  if (to.meta.requireAuth) {
    if (!token) {
      return next('/login'); // 로그인이 되지 않은 경우
    }

    const decodeToken = jwtDecode<DecodedToken>(token);
    const isAdmin = decodeToken.role === 'ADMIN';

    // ADMIN 페이지 접근 제한
    if (to.meta.requireAdmin && !isAdmin) {
      return next('/'); // ADMIN이 아니면 돌려보냄
    }
  }

  next(); // 나머지 요청에 대해선 통과
});

export default router;
