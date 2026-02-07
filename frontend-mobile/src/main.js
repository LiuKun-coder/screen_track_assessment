import { createSSRApp } from 'vue'
import App from './App.vue'
import pinia from './stores/index.js'

export function createApp() {
	const app = createSSRApp(App)

	// 使用 Pinia 状态管理
	app.use(pinia)

	return {
		app,
		pinia
	}
}
