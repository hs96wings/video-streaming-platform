import { defineStore } from "pinia";
import { jwtDecode } from "jwt-decode";

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem('token'),
        isLogin: false,
        isAdmin: false,
        username: ''
    }),
    actions: {
        updateAuthState(token) {
            if (token) {
                const { sub, role } = jwtDecode(token)
                this.token = token,
                this.isLogin = true
                this.username = sub
                this.isAdmin = (role === 'ADMIN')
                localStorage.setItem("token", token)
            } else {
                this.token = null
                this.isLogin = false
                this.username = ''
                this.isAdmin = false
                localStorage.removeItem('token')
            }
        },
        logout() {
            this.updateAuthState(null)
        }
    }
})