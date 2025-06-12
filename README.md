# 🎥 실시간 영상 스트리밍 웹 서비스

> 🔗 **[👉 배포된 사이트 바로가기 (https://lwasky.site)](https://lwasky.site)**

개인 프로젝트로 제작한 실시간 영상 스트리밍 및 채팅 기능이 있는 웹 플랫폼입니다.  
백엔드(Spring Boot)와 프론트(Vue 3)를 분리하여 개발하였고, 전체 서비스를 Docker로 배포했습니다.

---

## 📌 주요 기능

- ✅ JWT 기반 로그인 / 회원가입
- 💬 WebSocket 기반 1:1 채팅 기능
- 🎬 FFmpeg를 통한 영상 스트리밍 (HLS)
- 📝 영상별 댓글 작성 / 삭제
- 🌐 Nginx + SSL 인증서 적용 (Let's Encrypt)
- 🐳 Docker Compose를 활용한 통합 배포

---

## 🛠 기술 스택

### Backend

- Java 17, Spring Boot 3, Spring Security, JPA, MySQL, Redis, WebSocket

### Frontend

- Vue 3, Composition API, Pinia, Axios

### DevOps

- Docker, Docker Compose, Nginx, Let's Encrypt, Flask, FFmpeg
