<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" sm="4" md="6">
        <v-card>
          <v-card-title class="text-h5 text-center">영상 추가</v-card-title>
          <v-card-text>
            <v-form @submit.prevent="upload">
              <v-text-field label="영상 제목" v-model="title" required> </v-text-field>
              <v-text-field label="영상 설명" v-model="description" required> </v-text-field>
              <v-file-input label="영상 파일" @change="onFileChange"> </v-file-input>
              <v-btn type="submit" color="primary" block>업로드</v-btn>
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

const router = useRouter();

const title = ref<string>('');
const description = ref<string>('');
const file = ref<File | null>(null);

function onFileChange(e: Event): void {
  const target = e.target as HTMLInputElement;

  if (target.files && target.files.length > 0) {
    file.value = target.files[0];
  } else {
    file.value = null;
  }
}

async function upload(): Promise<void> {
  if (!file.value) {
    alert('업로드할 파일을 선택해주세요');
    return;
  }

  const formData = new FormData();
  formData.append('title', title.value);
  formData.append('description', description.value);
  formData.append('file', file.value);

  try {
    await api.post(`/api/video/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    router.push('/admin');
  } catch (err: unknown) {
    console.error('업로드 실패:', err);
  }
}
</script>
