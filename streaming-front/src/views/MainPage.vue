<template>
  <div class="maintenance-container">
    <div class="content">
      <span class="emoji">🛠</span>
      <h1>공사중입니다</h1>
      <p>더 좋은 서비스를 위해 준비 중입니다.</p>
    </div>
  </div>
</template>

<style scoped>
/* 화면 정중앙 정렬 */
.maintenance-container {
  height: 100vh; /* 화면 전체 높이 */
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  background-color: #f9f9f9; /* 배경색 (선택) */
}

/* 이모티콘 컬러 강제 설정 */
.emoji {
  font-size: 4rem; /* 크기 키우기 */
  /* 순서대로: 애플, 윈도우11/10, 안드로이드/리눅스 이모지 폰트 */
  font-family: 'Apple Color Emoji', 'Segoe UI Emoji', 'Noto Color Emoji', sans-serif;
}

h1 {
  margin-top: 10px;
  font-size: 2rem;
  color: #333;
}
</style>
