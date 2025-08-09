<template>
  <div>
    <nav class="admin-nav">
      <router-link to="/admin/videos" class="nav-link" exact-active-class="active">영상 관리</router-link>
      <router-link to="/admin/stats" class="nav-link" exact-active-class="active">방문 통계</router-link>
    </nav>
    <router-view />
  </div>
</template>

<style scoped>
.admin-nav {
  display: flex;
  border-bottom: 2px solid #ccc;
  margin-bottom: 1rem;
}

.nav-link {
  padding: 0.5rem 1rem;
  text-decoration: none;
  color: #333;
  border-bottom: 2px solid transparent;
  transition: border-bottom 0.2s;
}

.nav-link:hover {
  border-bottom: 2px solid #888;
}

.router-link-exact-active,
.active {
  border-bottom: 2px solid #007bff;
  font-weight: bold;
  color: #007bff;
}
</style>
