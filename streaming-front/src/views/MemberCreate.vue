<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" sm="4" md="6">
        <v-card>
          <v-card-title class="text-h5 text-center">회원가입</v-card-title>
          <v-card-text>
            <v-form @submit.prevent="create">
              <v-text-field v-model="userid" label="아이디" required> </v-text-field>
              <v-text-field v-model="password" label="비밀번호" type="password" required> </v-text-field>
              <v-btn type="submit" color="primary" block>회원가입</v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import api from '@/api/axios';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

interface SignupRequest {
  userid: string;
  password: string;
}

interface SignupResponse {
  token: string;
}

const userid = ref<string>('');
const password = ref<string>('');

const router = useRouter();
const auth = useAuthStore();

async function create(): Promise<void> {
  try {
    const createData: SignupRequest = {
      userid: userid.value,
      password: password.value,
    };
    const res = await api.post<SignupResponse>(`/api/auth/signup`, createData);

    auth.updateAuthState(res.data.token);
    router.push('/');
  } catch (error: unknown) {
    if (error instanceof Error) {
      console.error('회원가입 실패:', error);
    } else {
      console.error('회원가입 실패: 알 수 없는 오류', error);
    }
  }
}
</script>
