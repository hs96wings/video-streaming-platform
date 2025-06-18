import { defineStore } from "pinia";

export const useSnackbarStore = defineStore('snackbar', {
    state: () => ({
        show: false,
        message: '',
        color: 'error', // 에러 메시지이므로 기본 색상은 'error'
        timeout: 3000, // 3초 후 자동 닫힘
    }),
    actions: {
        showSnackbar(message, color = 'error', timeout = 3000) {
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