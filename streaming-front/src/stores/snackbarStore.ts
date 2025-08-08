import { defineStore } from "pinia";

interface SnackbarState {
    show: boolean,
    message: string | null,
    color: string,
    timeout: number,
}

export const useSnackbarStore = defineStore('snackbar', {
    state: (): SnackbarState => ({
        show: false,
        message: '',
        color: 'error', // 에러 메시지이므로 기본 색상은 'error'
        timeout: 3000, // 3초 후 자동 닫힘
    }),
    actions: {
        showSnackbar(message: string | null, color = 'error', timeout = 3000) {
            this.message = message
            this.color = color
            this.timeout = timeout
            this.show = true;
        },
        hideSnackbar() {
            this.show = false
            this.message = ''
        }
    }
})