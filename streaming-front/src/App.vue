<template>
  <v-app>
    <HeaderComponent />
    <v-main>
      <router-view />
    </v-main>

    <v-snackbar
      v-model="snackbarStore.show"
      :timeout="snackbarStore.timeout"
      :color="snackbarStore.color"
      location="bottom right"
    >
      {{ snackbarStore.message }}
      <template v-slot:actions>
        <v-btn color="white" variant="text" @click="snackbarStore.hideSnackbar()"> 닫기 </v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>

<script setup>
import { onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import HeaderComponent from './components/HeaderComponent.vue';
import { useSnackbarStore } from '@/stores/snackbarStore';
import api from './api/axios';
import { useRouter } from 'vue-router';

const auth = useAuthStore();
const snackbarStore = useSnackbarStore();
const router = useRouter();

onMounted(async () => {
  const token = auth.token;

  if (token) {
    try {
      await api.get(`/api/auth/validate`);
      auth.updateAuthState(token);
    } catch (error) {
      if (error instanceof Error) {
        snackbarStore.showSnackbar('세션이 만료되었습니다. 다시 로그인 해주세요.', 'warning');
        auth.logout();
        router.replace('/login');
      } else {
        snackbarStore.showSnackbar(`오류가 발생했습니다. ${error}`, 'error');
      }
    }
  }
});
</script>
