# ffmpeg를 사용한 영상 스트리밍 프로젝트

- Spring Boot: 사용자, 동영상 메타데이터 관리(CRUD)
- Vue.js: 프론트엔드, 로그인/회원가입, 동영상 목록/상세 페이지, UI
- Flask: ffmpeg으로 동영상 처리, 썸네일 생성
- MySQL: 사용자/동영상/댓글 등 저장
- Redis: 작업 큐 브로커 및 결과 캐싱, 메타데이터나 플레이리스트 캐시

## 깃허브에 commit할 땐 아래와 같이 명시함

- [Server] streaming-server (spring)
- [Front] streaming-front (vue)
- [Transcoder] streaming-transcoder (flask)
- [Total] 두 개 이상의 폴더에 변화가 있을 때
