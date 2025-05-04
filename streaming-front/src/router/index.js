import { createRouter, createWebHistory } from "vue-router"
import MemberCreate from '@/views/MemberCreate.vue'
import HomePage from '@/views/HomePage.vue'

const routes = [
    { path: '/', name: 'HomePage', component: HomePage },
    { path: '/join', name: 'MemberCreate', component: MemberCreate }
]

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;