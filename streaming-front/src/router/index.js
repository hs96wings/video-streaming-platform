import { createRouter, createWebHistory } from "vue-router"
import MemberCreate from '@/views/MemberCreate.vue'
import MemberLogin from '@/views/MemberLogin.vue'
import HomePage from '@/views/HomePage.vue'

const routes = [
    { path: '/', name: 'HomePage', component: HomePage },
    { path: '/create', name: 'MemberCreate', component: MemberCreate },
    { path: '/login', name: 'MemberLogin', component: MemberLogin }
]

const router = createRouter({
    history: createWebHistory(),
    routes
});

export default router;