package org.example.sc_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import org.example.sc_backend.common.utils.AdminAuthUtils;
import org.example.sc_backend.common.utils.Result;
import org.example.sc_backend.dto.TrackUploadDTO;
import org.example.sc_backend.entity.BizTrack;
import org.example.sc_backend.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 轨迹控制器
 */
@RestController
@RequestMapping("/tracks")
public class TrackController {

    @Autowired
    private TrackService trackService;

    /**
     * 获取用户某日期的轨迹
     */
    @GetMapping("/byDate")
    public Result<List<BizTrack>> getTracksByDate(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(required = false) Long userId) {
        Long currentUserId = (Long) request.getAttribute("userId");
        Long targetUserId = currentUserId;
        if (userId != null && !userId.equals(currentUserId)) {
            AdminAuthUtils.requireAdmin(request);
            targetUserId = userId;
        }
        List<BizTrack> tracks = trackService.getUserTracksByDate(targetUserId, date);
        return Result.success(tracks);
    }

    /**
     * 获取用户轨迹列表
     */
    @GetMapping("/list")
    public Result<IPage<BizTrack>> getUserTracks(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<BizTrack> result = trackService.getUserTracks(userId, page, pageSize);
        return Result.success(result);
    }

    /**
     * 获取轨迹详情
     */
    @GetMapping("/{id}")
    public Result<BizTrack> getTrackDetail(@PathVariable Long id) {
        BizTrack track = trackService.getTrackDetail(id);
        return Result.success(track);
    }

    /**
     * 上传轨迹点
     */
    @PostMapping("/upload")
    public Result<BizTrack> uploadTrackPoints(HttpServletRequest request,
                                              @RequestBody TrackUploadDTO uploadDTO) {
        Long userId = (Long) request.getAttribute("userId");
        BizTrack track = trackService.uploadTrackPoints(userId, uploadDTO);
        return Result.success(track);
    }
}
