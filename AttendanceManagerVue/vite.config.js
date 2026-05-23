import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      '@antv/g2': fileURLToPath(new URL('./node_modules/@antv/g2/lib/index.js', import.meta.url)),
      '@antv/component': fileURLToPath(new URL('./node_modules/@antv/component/lib/index.js', import.meta.url)),
      '@antv/util': fileURLToPath(new URL('./node_modules/@antv/util/lib/index.js', import.meta.url))
    }
  },
  server: {
    host: 'localhost',
    port: 9332
  },
  preview: {
    host: 'localhost',
    port: 9332
  }
})
