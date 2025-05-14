<template>
  <v-app>
    <HeaderComponent />
    <v-main>
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup>
import { onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import HeaderComponent from './components/HeaderComponent.vue'

const auth = useAuthStore()

onMounted(() => {
  auth.updateAuthState(auth.token) // 초기화
})

</script>