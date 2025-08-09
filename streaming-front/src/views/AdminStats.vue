<template>
  <Line :data="chartData" :options="chartOptions" />
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { Line } from 'vue-chartjs';
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  LineElement,
  PointElement,
  CategoryScale,
  LinearScale,
  type ChartData,
  type ChartOptions,
} from 'chart.js';
import api from '@/api/axios';
import { useSnackbarStore } from '@/stores/snackbarStore';
import { useRouter } from 'vue-router';
import { isAxiosError } from 'axios';

interface DailyVisitStat {
  date: string;
  uniqueVisitorCount: number;
}

ChartJS.register(Title, Tooltip, Legend, LineElement, PointElement, CategoryScale, LinearScale);

const chartData = ref<ChartData<'line'>>({ labels: [], datasets: [] });
const chartOptions: ChartOptions<'line'> = {
  responsive: true,
  plugins: {
    legend: {
      display: true,
      position: 'top',
    },
    title: {
      display: true,
      text: '일별 방문자 수 추이',
    },
  },
};

const snackBar = useSnackbarStore();
const router = useRouter();

const fetchAndSetChartData = async (): Promise<void> => {
  try {
    const visitStats = await api.get<DailyVisitStat[]>(`/api/admin/stats/visits/daily`);

    chartData.value = {
      labels: visitStats.map((item) => item.date),
      datasets: [
        {
          label: '일별 방문자 수',
          data: visitStats.map((item) => item.uniqueVisitorCount),
          borderColor: '#42A5F5',
          backgroundColor: 'rgba(66, 165, 245, 0.2)',
          tension: 0.3,
          fill: true,
        },
      ],
    };
  } catch (err: unknown) {
    console.error('API 요청 실패:', err);

    if (isAxiosError(err) && err.response?.status === 403) {
      snackBar.showSnackbar('권한이 없습니다. 다시 로그인 해주세요.', 'warning');
      router.push('/login');
    } else {
      snackBar.showSnackbar('통계 데이터를 불러오는 데 실패했습니다.', 'error');
    }
  }
};

onMounted(async () => {
  fetchAndSetChartData();
});
</script>
