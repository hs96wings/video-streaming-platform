# 🎥 실시간 영상 스트리밍 웹 서비스

> 🔗 [👉 배포된 사이트 (현재 없음)]

실시간 영상 스트리밍 및 채팅 기능을 갖춘 웹 플랫폼을 개인 프로젝트로 개발했습니다.  
Spring Boot 기반의 백엔드와 Vue 3 + Composition API 기반의 프론트엔드를 분리하여 개발하였으며, 기존 JavaScript 기반이었던 프론트엔드 코드를 TypeScript로 점진적으로 전환하여 코드의 안정성과 유지보수성을 향상시켰습니다.  
FFmpeg를 활용한 HLS 영상 변환, WebSocket 기반 채팅, SSE 기반 알림 기능을 포함하고 있습니다.  
모든 서비스는 Docker Compose를 통해 통합 배포하였고, Nginx와 Let's Encrypt로 HTTPS 환경을 구축했습니다.

---

## 📌 주요 기능

- ✅ JWT 기반 로그인 / 회원가입
- 💬 WebSocket 기반 1:1 채팅 기능
- 📡 SSE 기반 실시간 읽지 않은 메시지 알림
- 🎬 FFmpeg를 통한 영상 스트리밍 (HLS)
- 📝 영상별 댓글 작성 / 삭제
- 🌐 Nginx + SSL 인증서 적용 (Let's Encrypt)
- 🐳 Docker Compose를 활용한 통합 배포
- ✨ TypeScript 적용을 통한 타입 안정성 확보

---

## 🛠 기술 스택

### Backend

- Java 17, Kotlin, Spring Boot 3, Spring Security, JPA, MySQL, Redis, WebSocket, SSE

### Frontend

- Vue 3(Composition API), TypeScript, Pinia, Axios, Vuetify

### DevOps

- Docker, Docker Compose, Nginx, Let's Encrypt, Flask, FFmpeg
