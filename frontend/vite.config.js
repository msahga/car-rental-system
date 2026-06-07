import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import history from 'connect-history-api-fallback'

/**
 * Vite配置文件
 * 
 * 功能说明：
 * 1. 配置Vue插件
 * 2. 配置路径别名
 * 3. 配置开发服务器
 * 4. 配置构建选项
 * 
 * @author 毕业设计项目
 * @version 1.0.0
 */
export default defineConfig({
  // Vue插件
  plugins: [vue()],
  
  // 路径别名配置
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  
  // 开发服务器配置
  server: {
    port: 3000, // 开发服务器端口
    host: '127.0.0.1', // 仅本地访问，避免 IPv6 问题
    open: false, // 手动打开浏览器避免缓存
    cors: true, // 允许跨域
    // API代理配置（解决开发环境跨域问题）
    proxy: {
      '/api': {
        target: 'http://localhost:8081/api', // 后端API地址（包含context-path）
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/images': {
        target: 'http://localhost:8081/api',
        changeOrigin: true
      }
    },
    // 支持 Vue Router history 模式
    // 注意：不设置 { before: true }，确保 Vite 代理先处理 /api 请求
    configureServer(server) {
      server.middlewares.use(
        history({
          index: '/index.html',
          verbose: false,
          htmlAcceptHeaders: ['text/html'],
          disableDotRule: false,
          // 跳过 API 请求，避免 history fallback 拦截后端代理
          rewrites: []
        })
      )
    }
  },
  
  // 构建配置
  build: {
    outDir: 'dist', // 输出目录
    assetsDir: 'assets', // 静态资源目录
    sourcemap: false, // 不生成sourcemap
    // 构建优化
    chunkSizeWarningLimit: 1500,
    rollupOptions: {
      output: {
        // 分包策略
        manualChunks: {
          'element-plus': ['element-plus'],
          'vue-vendor': ['vue', 'vue-router', 'pinia']
        }
      }
    }
  }
})