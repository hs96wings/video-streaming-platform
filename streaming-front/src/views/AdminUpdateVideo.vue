<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" sm="4" md="6">
        <v-card>
          <v-card-title class="text-h5 text-center">영상 수정</v-card-title>
          <v-card-text>
            <v-form @submit.prevent="update">
              <v-text-field label="영상 제목" v-model="title" required> </v-text-field>
              <v-text-field label="영상 설명" v-model="description" required> </v-text-field>
              <v-btn type="submit" color="primary" block>수정</v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '@/api/axios';
import type { UploadRequest } from '@/types/api';

const route = useRoute();
const router = useRouter();

const idParam = route.params.id;
const id = Array.isArray(idParam) ? idParam[0] : idParam;
const title = ref<string>('');
const description = ref<string>('');

async function update(): Promise<void> {
  const videoData: UploadRequest = {
    title: title.value,
    description: description.value,
  };
  await api.patch(`/api/video/${id}`, videoData);
  router.push('/admin');
}

onMounted(async (): Promise<void> => {
  const data = await api.get(`/api/video/${id}`);
  title.value = data.title;
  description.value = data.description;
});
</script>
