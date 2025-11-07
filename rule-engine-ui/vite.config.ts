import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'
import UnoCSS from 'unocss/vite'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import ElementPlus from 'unplugin-element-plus/vite'
import { resolve } from 'path';

const pathResolve = (dir: string) => {
	return resolve(__dirname, '.', dir);
};

const alias: Record<string, string> = {
	'/@': pathResolve('./src/')
};

// https://vitejs.dev/config/
export default defineConfig({
  css: {
    preprocessorOptions: {
      scss: { api: 'modern-compiler' },
    }
  },
  plugins: [
    vue(),
    UnoCSS(),
    ElementPlus({}),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  server: {
    host: '0.0.0.0', // 服务器地址
    port: 8888, // 服务器端口号
    allowedHosts: true, // 允许所有域名和IP访问
    hmr: true, // 启用热更新
    proxy: {
      '/api': {
        target: "http://127.0.0.1:9999", // 目标服务器地址
        ws: true, // 是否启用 WebSocket
        changeOrigin: true, // 是否修改请求头中的 Origin 字段
        rewrite: (path) => path.replace(/^\/api/, ''),
      }
    },
  },
  resolve: { alias },
})
