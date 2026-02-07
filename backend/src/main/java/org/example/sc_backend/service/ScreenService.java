package org.example.sc_backend.service;

import org.example.sc_backend.dto.ScreenStatsVO;

/**
 * 大屏统计服务接口
 */
public interface ScreenService {
    /**
     * 获取大屏统计数据
     */
    ScreenStatsVO getScreenStats();
}
