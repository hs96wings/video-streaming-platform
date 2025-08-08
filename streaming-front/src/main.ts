import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from "./App.vue"
import vuetify from './plugins/vuetify'
import router from '@/router/index.js'
import axios from 'axios'
import { useSnackbarStore } from './stores/snackbarStore'

const app = createApp(App);
const pinia = createPinia()
app.use(pinia)
app.use(router);
app.use(vuetify);

axios.interceptors.request.use(
    config => {
        const token = localStorage.getItem("token")
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config;
    },
    error => {
        // 이 부분은 요청을 보내기 전의 에러 처리
        // 여기서는 서버 응답 에러를 처리하지 않으므로, 그대로 Promise.reject로 던져줌
        // 만약 요청 자체에 문제가 있었다면 여기에서 스낵바를 띄울수도 있음
        const snackbarStore = useSnackbarStore(pinia)
        if (!error.response && error.message === 'Network Error') { // 요청 자체가 나가지 않았을 경우
            snackbarStore.showSnackbar('네트워크 연결을 확인해주세요.', 'error')
        } else if (error.message) {
            snackbarStore.showSnackbar(`요청 오류: ${error.message}`, 'error')
        }
        return Promise.reject(error);
    }
)

axios.interceptors.response.use(
    (response) => {
        // 2xx 응답은 그대로 반환
        return response
    },
    (error) => {
        // 에러 응답을 받았을 때 처리하는 로직
        const snackbarStore = useSnackbarStore(pinia)

        if (error.response) {
            const status = error.response.status
            const message = error.response.data || '알 수 없는 오류가 발생했습니다'

            switch (status) {
                case 400: // Bad Request (IllegalArgumentException)
                    snackbarStore.showSnackbar(`잘못된 요청: ${message}`, 'warning')
                    break;
                case 404: // Not Found (EntityNotFoundException)
                    snackbarStore.showSnackbar(`찾을 수 없음: ${message}`, 'warning')
                    break;
                case 401: // Unauthorized
                    snackbarStore.showSnackbar('인증되지 않았습니다. 다시 로그인해주세요.', 'error')

                    localStorage.removeItem('token')
                    router.replace('/login')
                    return Promise.reject(error)
                case 403: // Forbidden
                    snackbarStore.showSnackbar('접근 권한이 없습니다.', 'error')
                    break;
                case 500: // Internal Server Error
                    snackbarStore.showSnackbar('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.', 'error')
                    break;
                default:
                    snackbarStore.showSnackbar(`오류 발생 (${status}): ${message}`, 'error')
                    break;
            }
        } else if (error.request) {
            // 요청은 전송되었으나 응답을 받지 못한 경우 (네트워크 오류 등)
            snackbarStore.showSnackbar('네트워크 오류가 발생했습니다. 인터넷 연결을 확인해주세요.')
        } else {
            // 요청 설정 중 오류 발생
            snackbarStore.showSnackbar(`오류: ${error.message}`, 'error')
        }
        return Promise.reject(error); // 에러를 다시 던져서 컴포넌트에서 추가 처리할 수 있게 됨
    }
)

app.mount('#app');
