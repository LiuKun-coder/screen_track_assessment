/**
 * 系统状态管理
 */
import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'

export const useSystemStore = defineStore('system', () => {
    // 全局加载状态
    const globalLoading = ref(false)
    const loadingText = ref('加载中...')

    // 系统信息
    const systemInfo = reactive({
        platform: '',
        statusBarHeight: 0,
        navBarHeight: 44,
        windowWidth: 0,
        windowHeight: 0,
        safeAreaBottom: 0
    })

    // 网络状态
    const networkType = ref('unknown')
    const isConnected = ref(true)

    // 初始化系统信息
    function initSystemInfo() {
        try {
            const info = uni.getSystemInfoSync()
            systemInfo.platform = info.platform
            systemInfo.statusBarHeight = info.statusBarHeight || 0
            systemInfo.windowWidth = info.windowWidth
            systemInfo.windowHeight = info.windowHeight

            // 安全区域底部高度
            if (info.safeAreaInsets) {
                systemInfo.safeAreaBottom = info.safeAreaInsets.bottom
            }

            // 微信小程序导航栏高度
            // #ifdef MP-WEIXIN
            const menuButtonInfo = uni.getMenuButtonBoundingClientRect()
            systemInfo.navBarHeight = (menuButtonInfo.top - systemInfo.statusBarHeight) * 2 + menuButtonInfo.height
            // #endif
        } catch (error) {
            console.error('获取系统信息失败:', error)
        }
    }

    // 监听网络状态
    function initNetworkListener() {
        // 获取当前网络状态
        uni.getNetworkType({
            success: (res) => {
                networkType.value = res.networkType
                isConnected.value = res.networkType !== 'none'
            }
        })

        // 监听网络变化
        uni.onNetworkStatusChange((res) => {
            networkType.value = res.networkType
            isConnected.value = res.isConnected

            if (!res.isConnected) {
                uni.showToast({
                    title: '网络已断开',
                    icon: 'none'
                })
            }
        })
    }

    // 显示全局加载
    function showLoading(text = '加载中...') {
        globalLoading.value = true
        loadingText.value = text
        uni.showLoading({
            title: text,
            mask: true
        })
    }

    // 隐藏全局加载
    function hideLoading() {
        globalLoading.value = false
        uni.hideLoading()
    }

    // 初始化
    function init() {
        initSystemInfo()
        initNetworkListener()
    }

    return {
        // 状态
        globalLoading,
        loadingText,
        systemInfo,
        networkType,
        isConnected,
        // 方法
        init,
        initSystemInfo,
        showLoading,
        hideLoading
    }
})
