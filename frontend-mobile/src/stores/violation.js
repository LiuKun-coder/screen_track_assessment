/**
 * 违规状态管理
 */
import { defineStore } from 'pinia'
import { ref, reactive } from 'vue'
import violationApi from '@/api/violation.js'

export const useViolationStore = defineStore('violation', () => {
    // 状态
    const myViolations = ref([])
    const publicViolations = ref([])
    const violationDetail = ref(null)
    const loading = ref(false)

    // 筛选条件
    const filters = reactive({
        type: '',
        place: '',
        time: '',
        status: ''
    })

    // 分页
    const pagination = reactive({
        page: 1,
        pageSize: 10,
        total: 0,
        hasMore: true
    })

    // 获取我的违规记录
    async function fetchMyViolations(reset = false) {
        if (reset) {
            pagination.page = 1
            myViolations.value = []
            pagination.hasMore = true
        }

        if (!pagination.hasMore || loading.value) return

        loading.value = true
        try {
            const res = await violationApi.getMyViolations({
                ...filters,
                page: pagination.page,
                pageSize: pagination.pageSize
            })

            const { list, total } = res.data
            myViolations.value = reset ? list : [...myViolations.value, ...list]
            pagination.total = total
            pagination.hasMore = myViolations.value.length < total
            pagination.page++

            return { success: true }
        } catch (error) {
            console.error('获取违规记录失败:', error)
            return { success: false, error: error.message }
        } finally {
            loading.value = false
        }
    }

    // 获取公开违规通报
    async function fetchPublicViolations(reset = false) {
        if (reset) {
            pagination.page = 1
            publicViolations.value = []
            pagination.hasMore = true
        }

        if (!pagination.hasMore || loading.value) return

        loading.value = true
        try {
            const res = await violationApi.getViolations({
                ...filters,
                page: pagination.page,
                pageSize: pagination.pageSize
            })

            const { list, total } = res.data
            publicViolations.value = reset ? list : [...publicViolations.value, ...list]
            pagination.total = total
            pagination.hasMore = publicViolations.value.length < total
            pagination.page++

            return { success: true }
        } catch (error) {
            console.error('获取违规通报失败:', error)
            return { success: false, error: error.message }
        } finally {
            loading.value = false
        }
    }

    // 获取违规详情
    async function fetchViolationDetail(id) {
        loading.value = true
        try {
            const res = await violationApi.getViolationDetail(id)
            violationDetail.value = res.data
            return { success: true, data: res.data }
        } catch (error) {
            console.error('获取违规详情失败:', error)
            return { success: false, error: error.message }
        } finally {
            loading.value = false
        }
    }

    // 设置筛选条件
    function setFilters(newFilters) {
        Object.assign(filters, newFilters)
    }

    // 清空筛选条件
    function clearFilters() {
        filters.type = ''
        filters.place = ''
        filters.time = ''
        filters.status = ''
    }

    // 重置状态
    function reset() {
        myViolations.value = []
        publicViolations.value = []
        violationDetail.value = null
        pagination.page = 1
        pagination.total = 0
        pagination.hasMore = true
        clearFilters()
    }

    return {
        // 状态
        myViolations,
        publicViolations,
        violationDetail,
        loading,
        filters,
        pagination,
        // 方法
        fetchMyViolations,
        fetchPublicViolations,
        fetchViolationDetail,
        setFilters,
        clearFilters,
        reset
    }
})
