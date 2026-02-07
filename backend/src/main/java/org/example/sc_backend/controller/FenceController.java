package org.example.sc_backend.controller;

import org.example.sc_backend.common.utils.Result;
import org.example.sc_backend.entity.BizFence;
import org.example.sc_backend.service.FenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 电子围栏控制器
 */
@RestController
@RequestMapping("/fences")
public class FenceController {

    @Autowired
    private FenceService fenceService;

    /**
     * 获取所有围栏
     */
    @GetMapping
    public Result<List<BizFence>> getAllFences() {
        List<BizFence> fences = fenceService.getAllFences();
        return Result.success(fences);
    }

    /**
     * 获取围栏详情
     */
    @GetMapping("/{id}")
    public Result<BizFence> getFenceDetail(@PathVariable Long id) {
        BizFence fence = fenceService.getFenceDetail(id);
        return Result.success(fence);
    }

    /**
     * 添加围栏 (管理员)
     */
    @PostMapping
    public Result<Void> addFence(@RequestBody BizFence fence) {
        fenceService.addFence(fence);
        return Result.success();
    }

    /**
     * 更新围栏 (管理员)
     */
    @PutMapping("/{id}")
    public Result<Void> updateFence(@PathVariable Long id, @RequestBody BizFence fence) {
        fence.setId(id);
        fenceService.updateFence(fence);
        return Result.success();
    }

    /**
     * 删除围栏 (管理员)
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteFence(@PathVariable Long id) {
        fenceService.deleteFence(id);
        return Result.success();
    }
}
