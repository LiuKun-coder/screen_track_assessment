package org.example.sc_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.sc_backend.common.exception.BusinessException;
import org.example.sc_backend.entity.BizTrack;
import org.example.sc_backend.mapper.BizTrackMapper;
import org.example.sc_backend.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 轨迹服务实现类
 */
@Service
public class TrackServiceImpl implements TrackService {

    @Autowired
    private BizTrackMapper trackMapper;

    @Override
    public List<BizTrack> getUserTracksByDate(Long userId, LocalDate date) {
        LambdaQueryWrapper<BizTrack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizTrack::getUserId, userId)
                .eq(BizTrack::getDate, date)
                .orderByAsc(BizTrack::getStartTime);
        return trackMapper.selectList(wrapper);
    }

    @Override
    public IPage<BizTrack> getUserTracks(Long userId, Integer page, Integer pageSize) {
        Page<BizTrack> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<BizTrack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizTrack::getUserId, userId)
                .orderByDesc(BizTrack::getDate);
        return trackMapper.selectPage(p, wrapper);
    }

    @Override
    public BizTrack getTrackDetail(Long id) {
        BizTrack track = trackMapper.selectById(id);
        if (track == null) {
            throw new BusinessException(404, "轨迹记录不存在");
        }
        return track;
    }
}
