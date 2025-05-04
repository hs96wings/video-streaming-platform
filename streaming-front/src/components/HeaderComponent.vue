<template>
    <v-app-bar app dark>
        <v-container>
            <v-row align="center">
                <v-col class="d-flex justify-start">
                    <v-btn :to="{path: '/list'}">영상 목록</v-btn>
                </v-col>
                <v-col text="text-center">
                    <v-btn :to="{path: '/'}">Home</v-btn>
                </v-col>
                <v-col class="d-flex justify-end">
                    <v-btn v-if="isAdmin" :to="{path: '/upload'}">영상 업로드</v-btn>
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
    props: {
        isLogin: {
            type: Boolean,
            default: false,
            required: true
        },
        isAdmin: {
            type: Boolean,
            default: false,
            required: true
        }
    },
    emits: ['logout'],
    methods: {
        doLogout() {
            this.$emit('logout');
        }
    }
}
</script>