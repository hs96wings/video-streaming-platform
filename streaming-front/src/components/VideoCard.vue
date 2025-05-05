<template>
    <v-card class="mx-auto" max-width="344">
        <v-img
            :src="video.thumbnailPath"
            height="180"
            class="white--text align-end"
        ></v-img>
        <v-card-title>{{ video.title }}</v-card-title>
        <v-card-subtitle>{{ formattedDate }}</v-card-subtitle>
    </v-card>
</template>

<script>
export default {
    name: 'videoCard',
    props: {
        video: {
            type: Object,
            required: true
        }
    },
    computed: {
        formattedDate() {
            const d = new Date(this.video.uploadedAt);
            const yy = d.getFullYear();
            const mm = String(d.getMonth() + 1).padStart(2, '0');
            const dd = String(d.getDate()).padStart(2, '0');
            const hh = String(d.getHours()).padStart(2, '0');
            const mi = String(d.getMinutes()).padStart(2, '0');
            return `${yy}.${mm}.${dd} ${hh}:${mi}`;
        }
    }
}
</script>