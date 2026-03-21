package org.example.sc_backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.sc_backend.dto.ViolationQueryDTO;
import org.example.sc_backend.dto.ViolationVO;
import org.example.sc_backend.entity.BizViolation;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 违规管理服务接口
 */
public interface ViolationService {
    /**
     * 分页查询违规列表
     */
    IPage<ViolationVO> getViolationPage(ViolationQueryDTO queryDTO);

    /**
     * 获取当前用户违规记录
     */
    IPage<ViolationVO> getMyViolations(Long userId, Integer page, Integer pageSize,
                                       String type, String status,
                                       LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取违规详情
     */
    ViolationVO getViolationDetail(Long id);

    /**
     * 添加违规记录 (管理员)
     */
    void addViolation(BizViolation violation);

    /**
     * 更新违规记录 (管理员)
     */
    void updateViolation(BizViolation violation);

    /**
     * 获取违规统计
     */
    Map<String, Object> getStatistics(Long userId);
}
