package org.example.sc_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.sc_backend.dto.ScreenStatsVO;
import org.example.sc_backend.entity.BizAppeal;
import org.example.sc_backend.entity.BizViolation;
import org.example.sc_backend.mapper.BizAppealMapper;
import org.example.sc_backend.mapper.BizViolationMapper;
import org.example.sc_backend.service.ScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 大屏统计服务实现类
 */
@Service
public class ScreenServiceImpl implements ScreenService {

    @Autowired
    private BizViolationMapper violationMapper;

    @Autowired
    private BizAppealMapper appealMapper;

    @Override
    public ScreenStatsVO getScreenStats() {
        ScreenStatsVO stats = new ScreenStatsVO();

        // 今日违规数
        stats.setTodayCount(violationMapper.countToday());

        // 本周违规数
        stats.setWeekCount(violationMapper.countThisWeek());

        // 总违规数
        Long total = violationMapper.selectCount(
                new LambdaQueryWrapper<BizViolation>().eq(BizViolation::getDeleted, 0)
        );
        stats.setTotalCount(total.intValue());

        // 待处理数量
        Long pending = violationMapper.selectCount(
                new LambdaQueryWrapper<BizViolation>()
                        .eq(BizViolation::getStatus, "pending")
                        .eq(BizViolation::getDeleted, 0)
        );
        stats.setPendingCount(pending.intValue());

        // 按类型分组统计
        stats.setTypeStats(violationMapper.groupByType());

        // 每日趋势
        stats.setDailyTrend(violationMapper.dailyTrend());

        // 申诉处理率
        Long totalAppeals = appealMapper.selectCount(new LambdaQueryWrapper<>());
        Long handledAppeals = appealMapper.selectCount(
                new LambdaQueryWrapper<BizAppeal>().ne(BizAppeal::getStatus, "pending")
        );
        if (totalAppeals > 0) {
            stats.setAppealRate((double) handledAppeals / totalAppeals * 100);
        } else {
            stats.setAppealRate(0.0);
        }

        return stats;
    }
}
