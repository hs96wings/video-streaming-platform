import { defineStore } from "pinia"
import { jwtDecode } from "jwt-decode"

interface DecodedToken {
    sub: string,
    role: string,
}

interface AuthState {
    token: string | null,
    isLogin: boolean,
    isAdmin: boolean,
    username: string | null
}

export const useAuthStore = defineStore('auth', {
    state: (): AuthState => ({
        token: localStorage.getItem('token'),
        isLogin: false,
        isAdmin: false,
        username: ''
    }),
    actions: {
        updateAuthState(token: string | null) {
            if (token) {
                const { sub, role } = jwtDecode<DecodedToken>(token)
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