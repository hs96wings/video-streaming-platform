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
      location="bottom right">
      {{ snackbarStore.message }}
      <template v-slot:actions>
        <v-btn
          color="white"
          variant="text"
          @click="snackbarStore.hideSnackbar()">
          닫기
        </v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>

<script setup>
import { onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import HeaderComponent from './components/HeaderComponent.vue'
import { useSnackbarStore } from '@/stores/snackbarStore';

const auth = useAuthStore()
const snackbarStore = useSnackbarStore()
onMounted(() => {
  auth.updateAuthState(auth.token) // 초기화
})

</script>