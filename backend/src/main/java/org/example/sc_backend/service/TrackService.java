package org.example.sc_backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.sc_backend.dto.TrackUploadDTO;
import org.example.sc_backend.entity.BizTrack;

import java.time.LocalDate;
import java.util.List;

/**
 * 轨迹服务接口
 */
public interface TrackService {
    /**
     * 获取用户某日期的轨迹列表
     */
    List<BizTrack> getUserTracksByDate(Long userId, LocalDate date);

    /**
     * 获取用户轨迹分页列表
     */
    IPage<BizTrack> getUserTracks(Long userId, Integer page, Integer pageSize);

    /**
     * 获取轨迹详情
     */
    BizTrack getTrackDetail(Long id);

    /**
     * 上传轨迹点
     */
    BizTrack uploadTrackPoints(Long currentUserId, TrackUploadDTO uploadDTO);
}
