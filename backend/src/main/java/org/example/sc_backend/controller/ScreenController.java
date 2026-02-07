package org.example.sc_backend.controller;

import org.example.sc_backend.common.utils.Result;
import org.example.sc_backend.dto.ScreenStatsVO;
import org.example.sc_backend.service.ScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 大屏统计控制器
 */
@RestController
@RequestMapping("/screen")
public class ScreenController {

    @Autowired
    private ScreenService screenService;

    /**
     * 获取大屏统计数据
     */
    @GetMapping("/stats")
    public Result<ScreenStatsVO> getScreenStats() {
        ScreenStatsVO stats = screenService.getScreenStats();
        return Result.success(stats);
    }
}
