<template>
    <v-app-bar app dark>
        <v-container>
            <v-row align="center">
                <v-col class="d-flex justify-start">
                    <v-btn :to="{path: '/video/list'}">영상 목록</v-btn>
                </v-col>
                <v-col>
                    <v-btn :to="{path: '/'}">Home</v-btn>
                </v-col>
                <v-col class="d-flex justify-end">
                    <v-btn v-if="!isLogin" :to="{path: '/create'}">회원가입</v-btn>
                    <v-btn v-if="!isLogin" :to="{path: '/login'}">로그인</v-btn>
                    <v-btn v-if="isLogin" @click="doLogout">로그아웃</v-btn>
                </v-col>
            </v-row>
        </v-container>
    </v-app-bar>
</template>

<script>
export default {
    data() {
        return {
            isLogin: false,
        }
    },
    created() {
        const token = localStorage.getItem("token");
        if (token) {
            this.isLogin = true;
        }
    },
    methods: {
        doLogout() {
            localStorage.clear();
            window.location.reload();
        }
    }
}
</script>