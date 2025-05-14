<template>
    <div class="comments">
        <div v-for="c in comments" :key="c.id" class="comment">
            <div class="meta">
                <span>{{ c.authorName }}</span>
                <span>{{  c.createdAt.slice(0,19).replace('T',' ') }}</span>
                <v-btn icon v-if="c.authorName === currentUser" @click="deleteComment(c.id)">
                    <v-icon>mdi-delete</v-icon>
                </v-btn>
            </div>
        </div>
    </div>    
</template>
<script>
</script>