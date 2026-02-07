package org.example.sc_backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.sc_backend.dto.AppealSubmitDTO;
import org.example.sc_backend.entity.BizAppeal;

/**
 * 申诉服务接口
 */
public interface AppealService {
    /**
     * 提交申诉
     */
    void submitAppeal(Long userId, AppealSubmitDTO dto);

    /**
     * 获取用户申诉列表
     */
    IPage<BizAppeal> getMyAppeals(Long userId, Integer page, Integer pageSize);

    /**
     * 获取申诉详情
     */
    BizAppeal getAppealDetail(Long id);

    /**
     * 处理申诉 (管理员)
     */
    void handleAppeal(Long id, Long handlerId, String status, String replyContent);

    /**
     * 获取所有申诉列表 (管理员)
     */
    IPage<BizAppeal> getAllAppeals(String status, Integer page, Integer pageSize);
}
