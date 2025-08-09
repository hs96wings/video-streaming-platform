import axios, {
  type AxiosInstance,
  type InternalAxiosRequestConfig,
  type AxiosResponse,
  type AxiosRequestConfig,
} from 'axios';
import router from '@/router';
import { useAuthStore } from '@/stores/auth';
import { useSnackbarStore } from '@/stores/snackbarStore';

const api: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  // 요청 타임아웃
  timeout: 5000,
});

// --- 요청 인터셉터 ---
// 모든 요청이 보내지기 전에 실행됨
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Pinia 스토어는 인터셉터 내부에서 호출해야 함
    const authStore = useAuthStore();
    const token = authStore.token;

    // 토큰이 있다면 헤더에 추가
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    const snackbarStore = useSnackbarStore();
    snackbarStore.showSnackbar(error.message || '요청 중 오류가 발생했습니다.', 'error');
    return Promise.reject(error);
  }
);

// --- 응답 인터셉터 ---
// 모든 응답이 도착한 후에 실행됨
api.interceptors.response.use(
  (response: AxiosResponse) => {
    // 2xx 응답은 그대로 반환
    return response.data;
  },
  (error) => {
    const snackbarStore = useSnackbarStore();

    if (error.response) {
      const status = error.response.status;
      // 백엔드에서 보낸 에러 메시지가 객체일 수도 있으므로, string으로 변환
      const message =
        typeof error.response.data === 'string' ? error.response.data : JSON.stringify(error.response.data);

      switch (status) {
        case 400:
          snackbarStore.showSnackbar(`잘못된 요청: ${message}`, 'warning');
          break;
        case 401:
          snackbarStore.showSnackbar('인증 정보가 만료되었습니다. 다시 로그인해주세요.');
          localStorage.removeItem('token');
          router.replace('/login');
          break;
        case 403:
          snackbarStore.showSnackbar('접근 권한이 없습니다.', 'error');
          break;
        case 404:
          snackbarStore.showSnackbar(`요청한 리소스를 찾을 수 없습니다. (${message})`, 'warning');
          break;
        case 500:
          snackbarStore.showSnackbar('서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.', 'error');
          break;
        default:
          snackbarStore.showSnackbar(`오류 발생 (${status}): ${message}`, 'error');
          break;
      }
    } else if (error.request) {
      snackbarStore.showSnackbar('서버에서 응답이 없습니다. 인터넷 연결을 확인해주세요.', 'error');
    } else {
      snackbarStore.showSnackbar(`요청 설정 오류: ${error.message}`, 'error');
    }

    return Promise.reject(error);
  }
);

export default api as {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<T>;
  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T>;
  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T>;
  patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T>;
  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T>;
};
