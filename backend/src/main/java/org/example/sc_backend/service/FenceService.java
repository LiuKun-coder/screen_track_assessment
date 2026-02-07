package org.example.sc_backend.service;

import org.example.sc_backend.entity.BizFence;

import java.util.List;

/**
 * 电子围栏服务接口
 */
public interface FenceService {
    /**
     * 获取所有围栏
     */
    List<BizFence> getAllFences();

    /**
     * 获取围栏详情
     */
    BizFence getFenceDetail(Long id);

    /**
     * 添加围栏
     */
    void addFence(BizFence fence);

    /**
     * 更新围栏
     */
    void updateFence(BizFence fence);

    /**
     * 删除围栏
     */
    void deleteFence(Long id);
}
