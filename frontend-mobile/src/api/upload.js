/**
 * 文件上传相关 API
 */
import config from '@/config/index.js'
import { getToken } from '@/utils/auth.js'
import { API_ENDPOINTS } from '@/config/api.js'

const uploadApi = {
    /**
     * 上传文件
     * @param {string} filePath 本地文件路径
     * @returns {Promise<string>} 返回文件 URL
     */
    uploadFile(filePath) {
        const token = getToken()

        return new Promise((resolve, reject) => {
            uni.uploadFile({
                url: config.BASE_URL + API_ENDPOINTS.UPLOAD,
                filePath: filePath,
                name: 'file',
                header: {
                    'Authorization': token ? `Bearer ${token}` : ''
                },
                success: (res) => {
                    try {
                        const data = JSON.parse(res.data)
                        if (data.code === 200) {
                            resolve(data.data)  // 返回文件 URL
                        } else {
                            uni.showToast({
                                title: data.message || '上传失败',
                                icon: 'none'
                            })
                            reject(new Error(data.message || '上传失败'))
                        }
                    } catch (error) {
                        reject(new Error('响应解析失败'))
                    }
                },
                fail: (error) => {
                    uni.showToast({
                        title: '上传失败',
                        icon: 'none'
                    })
                    reject(error)
                }
            })
        })
    },

    /**
     * 批量上传文件
     * @param {array} filePaths 文件路径数组
     * @returns {Promise<array>} 返回文件 URL 数组
     */
    async uploadFiles(filePaths) {
        const uploadPromises = filePaths.map(path => this.uploadFile(path))
        return Promise.all(uploadPromises)
    }
}

export default uploadApi
