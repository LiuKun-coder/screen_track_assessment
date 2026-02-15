import request from '@/utils/request'

export function getUserList(params) {
    return request({
        url: '/users/list',
        method: 'get',
        params
    })
}

export function getUserDetail(id) {
    return request({
        url: `/users/${id}`,
        method: 'get'
    })
}

export function updateUser(id, data) {
    return request({
        url: `/users/${id}`,
        method: 'put',
        data
    })
}

export function updateUserStatus(id, status) {
    return request({
        url: `/users/${id}/status`,
        method: 'put',
        data: { status }
    })
}

export function searchUsers(keyword) {
    return request({
        url: '/users/search',
        method: 'get',
        params: { keyword }
    })
}
