package org.example.sc_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.sc_backend.common.utils.Result;
import org.example.sc_backend.dto.AppealSubmitDTO;
import org.example.sc_backend.entity.BizAppeal;
import org.example.sc_backend.service.AppealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 申诉控制器
 */
@RestController
@RequestMapping("/appeals")
public class AppealController {

    @Autowired
    private AppealService appealService;

    /**
     * 提交申诉
     */
    @PostMapping
    public Result<Void> submitAppeal(HttpServletRequest request, @Valid @RequestBody AppealSubmitDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        appealService.submitAppeal(userId, dto);
        return Result.success();
    }

    /**
     * 获取我的申诉列表
     */
    @GetMapping("/my")
    public Result<IPage<BizAppeal>> getMyAppeals(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<BizAppeal> result = appealService.getMyAppeals(userId, page, pageSize);
        return Result.success(result);
    }

    /**
     * 获取申诉详情
     */
    @GetMapping("/{id}")
    public Result<BizAppeal> getAppealDetail(@PathVariable Long id) {
        BizAppeal appeal = appealService.getAppealDetail(id);
        return Result.success(appeal);
    }

    /**
     * 获取所有申诉列表 (管理员)
     */
    @GetMapping("/list")
    public Result<IPage<BizAppeal>> getAllAppeals(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<BizAppeal> result = appealService.getAllAppeals(status, page, pageSize);
        return Result.success(result);
    }

    /**
     * 处理申诉 (管理员)
     */
    @PutMapping("/{id}/handle")
    public Result<Void> handleAppeal(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam String replyContent) {
        Long handlerId = (Long) request.getAttribute("userId");
        appealService.handleAppeal(id, handlerId, status, replyContent);
        return Result.success();
    }
}
