<template>
    <v-container>
        <v-row justify="center">
            <v-col cols="12" md="8">
                <v-card>
                    <v-card-title class="text-center text-h5">채팅</v-card-title>
                    <v-card-text>
                        <div class="chat-box" ref="chatBox">
                            <div
                                v-for="(msg, index) in messages"
                                :key="index"
                                :class="['chat-message', msg.senderUserid === auth.username ? 'sent' : 'received']">
                                <strong>{{ msg.senderUserid }}</strong>: {{ msg.message }}
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
import { ref, nextTick, onBeforeUnmount } from 'vue'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'
import { useAuthStore } from '@/stores/auth';
import { onBeforeRouteLeave } from 'vue-router';

const auth = useAuthStore()
const token = auth.token;

const messages = ref([])
const newMessage = ref('')
const chatBox = ref(null)

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
        const parseMessage = JSON.parse(message.body)
        messages.value.push(parseMessage)
        scrollToBottom()
    })
}

stompClient.activate()

function sendMessage() {
    if (newMessage.value.trim() === "") return
    const message = {
        senderUserid: auth.username,
        message: newMessage.value.trim()
    }
    stompClient.publish({
        destination: `/publish/1`,
        body: JSON.stringify(message)
    })
    newMessage.value = ''
}

function scrollToBottom() {
    nextTick(() => {
        if (chatBox.value) {
            chatBox.value.scrollTop = chatBox.value.scrollHeight
        }
    })
}

onBeforeUnmount(() => {
    stompClient.deactivate()
})

onBeforeRouteLeave((to, from, next) => {
    stompClient.deactivate()
    next()
})

</script>

<style>
.chat-box {
    height: 300px;
    overflow-y: auto;
    border: 1px solid #ddd;
    margin-bottom: 10px;
}

.chat-message {
    padding: 8px 12px;
    border-radius: 10px;
    max-width: fit-content;
}

.sent {
    text-align: right;
    background-color: #d1e7dd;
    margin-left: auto;
}

.received {
    text-align: left;
    background-color: #f8d7da;
    margin-right: auto;
}
</style>