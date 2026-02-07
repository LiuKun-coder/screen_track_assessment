import axios from 'axios'

const BASE_URL = 'http://localhost:8080/api'

/**
 * 上传文件
 * @param {File} file - 文件对象
 * @returns {Promise} - 文件URL
 */
export function uploadFile(file) {
    const formData = new FormData()
    formData.append('file', file)

    const token = localStorage.getItem('token')

    return axios({
        url: `${BASE_URL}/upload`,
        method: 'post',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data',
            'Authorization': token ? 'Bearer ' + token : ''
        }
    }).then(response => {
        const res = response.data
        if (res.code === 200) {
            return res.data // 返回文件URL
        } else {
            return Promise.reject(new Error(res.message || '上传失败'))
        }
    })
}
