import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

function stripVueUsePureAnnotations() {
  return {
    name: 'strip-vueuse-pure-annotations',
    enforce: 'pre',
    transform(code, id) {
      const normalizedId = id.replace(/\\/g, '/')
      if (!normalizedId.includes('/@vueuse/core/dist/index.js')) {
        return null
      }
      return {
        code: code.replace(/\/\*\s*#__PURE__\s*\*\//g, ''),
        map: null
      }
    }
  }
}

function manualVendorChunks(id) {
  const normalizedId = id.replace(/\\/g, '/')
  if (!normalizedId.includes('/node_modules/')) {
    return undefined
  }
  if (normalizedId.includes('/@antv/')) {
    const packageName = normalizedId.split('/node_modules/@antv/')[1]?.split('/')[0]
    return packageName ? `vendor-antv-${packageName}` : 'vendor-antv'
  }
  if (normalizedId.includes('/element-plus/') || normalizedId.includes('/@element-plus/')) {
    return 'vendor-element'
  }
  if (
    normalizedId.includes('/vue/') ||
    normalizedId.includes('/vue-router/') ||
    normalizedId.includes('/@vue/') ||
    normalizedId.includes('/@vueuse/')
  ) {
    return 'vendor-vue'
  }
  if (normalizedId.includes('/axios/')) {
    return 'vendor-request'
  }
  return 'vendor'
}

export default defineConfig({
  plugins: [stripVueUsePureAnnotations(), vue()],
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
  },
  build: {
    rolldownOptions: {
      output: {
        manualChunks: manualVendorChunks
      }
    }
  }
})
