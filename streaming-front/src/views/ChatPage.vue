<template>
    <v-container>
        <v-row justify="center">
            <v-col cols="12" md="8">
                <v-card>
                    <v-card-title class="text-center text-h5">채팅</v-card-title>
                    <v-card-text>
                        <div class="chat-box">
                            <div
                                v-for="(msg, index) in messages"
                                :key="index">
                                {{ msg }}
                            </div>
                        </div>
                        <v-text-field 
                            v-model="newMessage"
                            label="메시지 입력"
                            @keyup.enter="sendMessage"/>
                        <v-btn color="primary" block @click="sendMessage">전송</v-btn>
                    </v-card-text>
                </v-card>
            </v-col>
        </v-row>
    </v-container>
</template>

<script setup>
import { ref } from 'vue'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'
import { useAuthStore } from '@/stores/auth';

const auth = useAuthStore()
const token = auth.token;

const messages = ref([])
const newMessage = ref('')

// 1. STOMP 클라이언트 생성
const stompClient = new Client({
    // 2. SockJS를 통해 커넥션을 만듬
    connectHeaders: {'Authorization': `Bearer ${token}`},
    webSocketFactory: () => new SockJS(`${process.env.VUE_APP_API_BASE_URL}/connect`),
    debug: (str) => console.log('[STOMP]', str),
    reconnectDelay: 5000 // 연결 끊겼을 때 재연결 시도
})

// 3. 연결 후 콜백
stompClient.onConnect = () => {
    // 구독
    stompClient.subscribe(`/topic/1`, (message) => {
        messages.value.push(message.body)
    })
}

stompClient.activate()

function sendMessage() {
    if (newMessage.value.trim() === "") return
    stompClient.publish({
        destination: `/publish/1`,
        body: newMessage.value
    })
    newMessage.value = ''
}

</script>