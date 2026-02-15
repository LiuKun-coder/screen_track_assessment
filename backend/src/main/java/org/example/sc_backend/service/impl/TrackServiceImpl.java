package org.example.sc_backend.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.sc_backend.common.exception.BusinessException;
import org.example.sc_backend.dto.TrackUploadDTO;
import org.example.sc_backend.entity.BizTrack;
import org.example.sc_backend.mapper.BizTrackMapper;
import org.example.sc_backend.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
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

    @Override
    public BizTrack uploadTrackPoints(Long currentUserId, TrackUploadDTO uploadDTO) {
        if (uploadDTO == null || uploadDTO.getTrackPoints() == null || uploadDTO.getTrackPoints().isEmpty()) {
            throw new BusinessException(400, "轨迹点不能为空");
        }

        Long userId = uploadDTO.getUserId() != null ? uploadDTO.getUserId() : currentUserId;
        if (userId == null) {
            throw new BusinessException(400, "用户信息缺失");
        }

        LocalDate trackDate = parseTrackDate(uploadDTO);

        BizTrack existing = trackMapper.selectOne(
                new LambdaQueryWrapper<BizTrack>()
                        .eq(BizTrack::getUserId, userId)
                        .eq(BizTrack::getDate, trackDate)
                        .last("limit 1")
        );

        List<TrackUploadDTO.TrackPointDTO> mergedPoints = new ArrayList<>();
        if (existing != null && existing.getTrackPoints() != null && !existing.getTrackPoints().isBlank()) {
            mergedPoints.addAll(JSON.parseArray(existing.getTrackPoints(), TrackUploadDTO.TrackPointDTO.class));
        }
        mergedPoints.addAll(uploadDTO.getTrackPoints());
        mergedPoints.sort(Comparator.comparing(this::extractTimeForSort));

        LocalDateTime startTime = extractStartTime(mergedPoints);
        LocalDateTime endTime = extractEndTime(mergedPoints);
        BigDecimal distance = calculateDistance(mergedPoints);

        BizTrack target = existing == null ? new BizTrack() : existing;
        target.setUserId(userId);
        target.setDate(trackDate);
        target.setStartTime(startTime);
        target.setEndTime(endTime);
        target.setDistance(distance);
        target.setTrackPoints(JSON.toJSONString(mergedPoints));

        if (existing == null) {
            trackMapper.insert(target);
        } else {
            trackMapper.updateById(target);
        }

        return target;
    }

    private LocalDate parseTrackDate(TrackUploadDTO uploadDTO) {
        if (uploadDTO.getDate() != null && !uploadDTO.getDate().isBlank()) {
            return LocalDate.parse(uploadDTO.getDate());
        }
        LocalDateTime firstTime = extractTime(uploadDTO.getTrackPoints().get(0));
        return firstTime != null ? firstTime.toLocalDate() : LocalDate.now();
    }

    private LocalDateTime extractStartTime(List<TrackUploadDTO.TrackPointDTO> points) {
        return points.stream()
                .map(this::extractTime)
                .filter(java.util.Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
    }

    private LocalDateTime extractEndTime(List<TrackUploadDTO.TrackPointDTO> points) {
        return points.stream()
                .map(this::extractTime)
                .filter(java.util.Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
    }

    private LocalDateTime extractTimeForSort(TrackUploadDTO.TrackPointDTO point) {
        LocalDateTime time = extractTime(point);
        return time != null ? time : LocalDateTime.MIN;
    }

    private LocalDateTime extractTime(TrackUploadDTO.TrackPointDTO point) {
        String value = point.getTimestamp();
        if (value == null || value.isBlank()) {
            value = point.getTime();
        }
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException ignored) {
        }
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException ignored) {
        }
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException ignored) {
        }
        return null;
    }

    private BigDecimal calculateDistance(List<TrackUploadDTO.TrackPointDTO> points) {
        if (points.size() < 2) {
            return BigDecimal.ZERO;
        }
        double totalKm = 0;
        for (int index = 1; index < points.size(); index++) {
            TrackUploadDTO.TrackPointDTO previous = points.get(index - 1);
            TrackUploadDTO.TrackPointDTO current = points.get(index);

            Double lat1 = getLatitude(previous);
            Double lng1 = getLongitude(previous);
            Double lat2 = getLatitude(current);
            Double lng2 = getLongitude(current);

            if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
                continue;
            }
            totalKm += haversineKm(lat1, lng1, lat2, lng2);
        }
        return BigDecimal.valueOf(totalKm).setScale(2, RoundingMode.HALF_UP);
    }

    private Double getLatitude(TrackUploadDTO.TrackPointDTO point) {
        return point.getLatitude() != null ? point.getLatitude() : point.getLat();
    }

    private Double getLongitude(TrackUploadDTO.TrackPointDTO point) {
        return point.getLongitude() != null ? point.getLongitude() : point.getLng();
    }

    private double haversineKm(double lat1, double lng1, double lat2, double lng2) {
        final double radiusKm = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return radiusKm * c;
    }
}
