<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" sm="4" md="6">
        <v-card>
          <v-card-title class="text-h5 text-center">로그인</v-card-title>
          <v-card-text>
            <v-form @submit.prevent="doLogin">
              <v-text-field v-model="userid" label="아이디" required />
              <v-text-field v-model="password" label="비밀번호" type="password" required />
              <v-btn type="submit" color="primary" block>로그인</v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import api from '@/api/axios';
import { type LoginRequest, type LoginResponse } from '@/types/api';

const userid = ref<string>('');
const password = ref<string>('');

const router = useRouter();
const auth = useAuthStore();

async function doLogin() {
  try {
    const loginData: LoginRequest = {
      userid: userid.value,
      password: password.value,
    };

    const res = await api.post<LoginResponse>(`/api/auth/login`, loginData);

    auth.updateAuthState(res.data.token);
    router.push('/');
  } catch (error: unknown) {
    // 에러 인터셉터에서 스낵바로 사용자에게 피드백을 줬으므로, 콘솔에만 기록
    console.error('로그인 컴포넌트에서 잡은 에러:', error);
  }
}
</script>
