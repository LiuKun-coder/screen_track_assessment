package org.example.sc_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import org.example.sc_backend.common.utils.Result;
import org.example.sc_backend.dto.ViolationQueryDTO;
import org.example.sc_backend.dto.ViolationVO;
import org.example.sc_backend.entity.BizViolation;
import org.example.sc_backend.service.ViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 获取当前用户的违规记录
     */
    @GetMapping("/my")
    public Result<IPage<ViolationVO>> getMyViolations(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<ViolationVO> result = violationService.getMyViolations(userId, page, pageSize);
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
    public Result<Void> addViolation(@RequestBody BizViolation violation) {
        violationService.addViolation(violation);
        return Result.success();
    }

    /**
     * 更新违规记录 (管理员)
     */
    @PutMapping("/{id}")
    public Result<Void> updateViolation(@PathVariable Long id, @RequestBody BizViolation violation) {
        violation.setId(id);
        violationService.updateViolation(violation);
        return Result.success();
    }
}
