<template>
    <Line :data="chartData" :options="chartOptions" />
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { Line } from 'vue-chartjs';
import {
    Chart as ChartJS,
    Title, Tooltip, Legend, LineElement, PointElement, CategoryScale, LinearScale
} from 'chart.js'
import axios from 'axios';
import { useSnackbarStore } from '@/stores/snackbarStore';
import { useRouter } from 'vue-router';

ChartJS.register(
    Title, Tooltip, Legend,
    LineElement, PointElement,
    CategoryScale, LinearScale
)

const visitStats = ref([])
const chartData = ref({ labels: [], datasets: [] })
const chartOptions = { responsive: true, plugins: { legend: { display: true }}}
const snackBar = useSnackbarStore()
const router = useRouter()

onMounted(async () => {
    try {
        const response = await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/admin/stats/visits/daily`)
        if (Array.isArray(response.data)) {
            visitStats.value = response.data
        } else {
            console.warn('관리자 통계 응답이 배열이 아님:', response.data)
            visitStats.value = []
        }
    } catch (err) {
        console.err('API 요청 실패:', err)
        if (err.response?.status === 403) {
            snackBar.showSnackbar('권한이 없습니다. 다시 로그인해주세요', 'waring')
            router.push('/login')
        }
    }

    chartData.value = {
        labels: visitStats.value.map(item => item.date),
        datasets: [
            {
                label: '일별 방문자 수',
                data: visitStats.value.map(item => item.uniqueVisitorCount),
                tension: 0.3,
                fill: true
            }
        ]
    }
})
</script>