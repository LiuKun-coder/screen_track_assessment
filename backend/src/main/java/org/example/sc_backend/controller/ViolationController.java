package org.example.sc_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import org.example.sc_backend.common.utils.AdminAuthUtils;
import org.example.sc_backend.common.utils.Result;
import org.example.sc_backend.dto.ViolationQueryDTO;
import org.example.sc_backend.dto.ViolationVO;
import org.example.sc_backend.entity.BizViolation;
import org.example.sc_backend.service.ViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 违规管理控制器
 */
@RestController
@RequestMapping("/violations")
public class ViolationController {

    @Autowired
    private ViolationService violationService;

    /**
     * 获取违规列表 (大厅/管理员)
     */
    @GetMapping("/reports")
    public Result<IPage<ViolationVO>> getViolationReports(ViolationQueryDTO queryDTO) {
        IPage<ViolationVO> page = violationService.getViolationPage(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取违规统计（总违规/我的违规/待处理）
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(violationService.getStatistics(userId));
    }

    /**
     * 获取当前用户的违规记录
     */
    @GetMapping("/my")
    public Result<IPage<ViolationVO>> getMyViolations(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<ViolationVO> result = violationService.getMyViolations(userId, page, pageSize, type, status, startTime, endTime);
        return Result.success(result);
    }

    /**
     * 获取违规详情
     */
    @GetMapping("/{id}")
    public Result<ViolationVO> getViolationDetail(@PathVariable Long id) {
        ViolationVO vo = violationService.getViolationDetail(id);
        return Result.success(vo);
    }

    /**
     * 添加违规记录 (管理员)
     */
    @PostMapping
    public Result<Void> addViolation(HttpServletRequest request, @RequestBody BizViolation violation) {
        AdminAuthUtils.requireAdmin(request);
        violationService.addViolation(violation);
        return Result.success();
    }

    /**
     * 更新违规记录 (管理员)
     */
    @PutMapping("/{id}")
    public Result<Void> updateViolation(HttpServletRequest request, @PathVariable Long id, @RequestBody BizViolation violation) {
        AdminAuthUtils.requireAdmin(request);
        violation.setId(id);
        violationService.updateViolation(violation);
        return Result.success();
    }
}
