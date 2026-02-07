/**
 * 本地存储工具
 */
class Storage {
    constructor(prefix = 'cache_') {
        this.prefix = prefix
        this.defaultTTL = 5 * 60 * 1000 // 5分钟
    }

    /**
     * 设置缓存
     * @param {string} key 键名
     * @param {any} data 数据
     * @param {number} ttl 过期时间（毫秒）
     */
    set(key, data, ttl = this.defaultTTL) {
        const cacheData = {
            data,
            timestamp: Date.now(),
            ttl
        }
        try {
            uni.setStorageSync(this.prefix + key, cacheData)
        } catch (error) {
            console.error('存储设置失败:', error)
        }
    }

    /**
     * 获取缓存
     * @param {string} key 键名
     * @returns {any} 数据或 null
     */
    get(key) {
        try {
            const cacheData = uni.getStorageSync(this.prefix + key)
            if (!cacheData) return null

            const { data, timestamp, ttl } = cacheData
            // 检查是否过期
            if (ttl > 0 && Date.now() - timestamp > ttl) {
                this.remove(key)
                return null
            }

            return data
        } catch (error) {
            console.error('存储获取失败:', error)
            return null
        }
    }

    /**
     * 删除缓存
     * @param {string} key 键名
     */
    remove(key) {
        try {
            uni.removeStorageSync(this.prefix + key)
        } catch (error) {
            console.error('存储删除失败:', error)
        }
    }

    /**
     * 清除所有缓存
     */
    clear() {
        try {
            const keys = uni.getStorageInfoSync().keys
            keys.forEach(key => {
                if (key.startsWith(this.prefix)) {
                    uni.removeStorageSync(key)
                }
            })
        } catch (error) {
            console.error('存储清除失败:', error)
        }
    }
}

// 创建默认实例
const storage = new Storage()

// 简便方法 - 不带 TTL 的永久存储
export const setStorage = (key, data) => {
    try {
        uni.setStorageSync(key, data)
    } catch (error) {
        console.error('存储设置失败:', error)
    }
}

export const getStorage = (key) => {
    try {
        return uni.getStorageSync(key)
    } catch (error) {
        console.error('存储获取失败:', error)
        return null
    }
}

export const removeStorage = (key) => {
    try {
        uni.removeStorageSync(key)
    } catch (error) {
        console.error('存储删除失败:', error)
    }
}

export { Storage }
export default storage
