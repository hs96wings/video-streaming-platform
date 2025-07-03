import { defineConfig } from "vite"
import vue from '@vitejs/plugin-vue'
import vuetify from 'vite-plugin-vuetify'
import path from 'path'

export default defineConfig({
    plugins: [
        vue(),
        vuetify({ autoImport: true })
    ],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, './src'),
            'global': 'globalthis-polyfill'
        }
    },
    define: {
        global: 'globalThis'
    },
    server: {
        port: 3000
    },
    build: {
        outDir: 'dist'
    }
})