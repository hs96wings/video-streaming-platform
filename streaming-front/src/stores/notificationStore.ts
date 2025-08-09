import { defineStore } from 'pinia';
import { fetchEventSource, type EventSourceMessage } from '@microsoft/fetch-event-source';
import { useAuthStore } from './auth';

interface Notification {
  roomId: number;
  unreadCount: number;
}

interface NotificationState {
  notifications: Notification[];
  abortController: AbortController | null;
  isConnected: boolean;
  error: string | null;
}

export const useNotificationStore = defineStore('notification', {
  state: (): NotificationState => ({
    notifications: [], // 수신된 알림 저장
    abortController: null, // SSE 연결을 끊기 위한 AbortController 인스턴스
    isConnected: false, // SSE 연결 상태
    error: null, // 연결 오류 메시지
  }),
  actions: {
    async connectSSE() {
      // 이미 연결되어 있다면 다시 시도하지 않음
      if (this.isConnected && this.abortController) {
        console.warn('SSE is already connected.');
        return;
      }

      // 기존 연결이 있다면 안전하게 종료하고 새로 시작 (재연결 시 유용)
      this.disconnectSSE();

      // 인증 토큰 가져오기
      const authStore = useAuthStore();
      const accessToken = authStore.token;

      if (!accessToken) {
        this.error = 'SSE 연결을 위한 인증 토큰이 없습니다. 로그인해주세요.';
        console.error(this.error);
        return;
      }

      const sseUrl = `${import.meta.env.VITE_API_BASE_URL}/api/sse/connect`;

      // 새로운 AbortController를 생성하여 연결 제어
      const controller = new AbortController();
      this.abortController = controller; // 스토어 상태에 저장

      try {
        await fetchEventSource(sseUrl, {
          // AbortController의 signal을 fetchEventSource에 전달하여 연결 제어
          signal: controller.signal,
          // Authorization 헤더에 인증 토큰 포함
          headers: {
            Authorization: `Bearer ${accessToken}`,
            Accept: 'text/event-stream', // SSE 요청임을 명시
          },
          // 연결이 열렸을 때 호출됨
          onopen: async (response: Response) => {
            if (response.ok && response.headers.get('content-type')?.includes('text/event-stream')) {
              this.isConnected = true;
              this.error = null;
              console.log('SSE 연결 성공!');
            } else if (response.status === 401 || response.status === 403) {
              // 인증 실패 또는 권한 없음 오류 처리
              console.error('SSE 연결 실패: 인증되지 않았거나 권한이 없습니다.');
              this.error = '인증 오류: 다시 로그인해주세요.';
              // TODO: 필요하다면 로그인 페이지로 리다이렉트 처리
            } else {
              // 예상치 못한 응답 상태 코드 처리
              console.error('예상치 못한 SSE 연결 응답:', response);
              this.error = `SSE 연결 실패 (상태 코드: ${response.status})`;
              this.disconnectSSE();
            }
          },
          // 메시지가 수신될 때마다 호출
          onmessage: (event: EventSourceMessage) => {
            console.log('SSE 메시지 수신:', event.data);
            try {
              // event.data의 타입을 Notification으로 파싱
              const newNotification: Notification = JSON.parse(event.data);
              this.notifications.push(newNotification); // 알림 배열에 추가
            } catch (e) {
              console.error('SSE 메시지 파싱 실패:', e);
            }
          },
          // 연결이 닫혔을 때 호출
          onclose: () => {
            this.isConnected = false;
            console.log('SSE 연결 종료됨.');
          },
          // 에러 발생 시 호출
          onerror: (err: any) => {
            this.isConnected = false;
            this.error = 'SSE 연결 오류: ' + err.message;
            console.error('SSE 연결 중 오류 발생:', err);
            this.disconnectSSE();
          },
        });
      } catch (e: unknown) {
        // 사용자가 의도적으로 연결을 끊었을 때 발생하는 AbortError는 무시
        if (e instanceof Error && e.name === 'AbortError') {
          console.log('SSE 연결이 사용자 요청으로 중단되었습니다');
        } else {
          this.isConnected = false;
          this.error = 'SSE 연결 시작 실패';
          console.error('SSE 연결 시작 중 예외 발생:', e);
        }
      }
    },

    // SSE 연결을 명시적으로 종료하는 액션
    disconnectSSE() {
      if (this.abortController) {
        this.abortController.abort(); // 연결 중단
        this.abortController = null;
        this.isConnected = false;
        console.log('SSE 연결을 끊었습니다');
      }
    },

    // 알림 목록을 초기화하는 액션
    clearNotifications() {
      this.notifications = [];
    },
  },
});
