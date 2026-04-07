package org.example.sc_backend.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.sc_backend.common.exception.BusinessException;
import org.example.sc_backend.dto.AppealSubmitDTO;
import org.example.sc_backend.entity.BizAppeal;
import org.example.sc_backend.entity.BizViolation;
import org.example.sc_backend.mapper.BizAppealMapper;
import org.example.sc_backend.mapper.BizViolationMapper;
import org.example.sc_backend.service.AppealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 申诉服务实现类
 */
@Service
public class AppealServiceImpl implements AppealService {

    @Autowired
    private BizAppealMapper appealMapper;

    @Autowired
    private BizViolationMapper violationMapper;

    @Override
    @Transactional
    public void submitAppeal(Long userId, AppealSubmitDTO dto) {
        // 校验违规记录
        BizViolation violation = violationMapper.selectById(dto.getViolationId());
        if (violation == null) {
            throw new BusinessException(404, "违规记录不存在");
        }
        if (!userId.equals(violation.getUserId())) {
            throw new BusinessException(403, "只能申诉自己的违规记录");
        }
        if (violation.getCanAppeal() == null || violation.getCanAppeal() != 1) {
            throw new BusinessException(400, "该违规记录不可申诉");
        }
        if (!"none".equals(violation.getAppealStatus())) {
            throw new BusinessException(400, "已提交过申诉，不可重复提交");
        }

        // 创建申诉记录
        BizAppeal appeal = new BizAppeal();
        appeal.setViolationId(dto.getViolationId());
        appeal.setUserId(userId);
        appeal.setAppealNumber(generateAppealNumber());
        appeal.setPlatform(dto.getPlatform());
        appeal.setReason(dto.getReason());
        appeal.setDescription(dto.getDescription());
        appeal.setContactPhone(dto.getContactPhone());
        appeal.setStatus("pending");

        if (dto.getEvidenceFiles() != null && !dto.getEvidenceFiles().isEmpty()) {
            appeal.setEvidenceFiles(JSON.toJSONString(dto.getEvidenceFiles()));
        }

        appealMapper.insert(appeal);

        // 更新违规记录申诉状态
        violation.setAppealStatus("pending");
        violationMapper.updateById(violation);
    }

    @Override
    public IPage<BizAppeal> getMyAppeals(Long userId, Integer page, Integer pageSize) {
        Page<BizAppeal> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<BizAppeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizAppeal::getUserId, userId)
                .orderByDesc(BizAppeal::getCreateTime);
        return appealMapper.selectPage(p, wrapper);
    }

    @Override
    public BizAppeal getAppealDetail(Long id) {
        BizAppeal appeal = appealMapper.selectById(id);
        if (appeal == null) {
            throw new BusinessException(404, "申诉记录不存在");
        }
        return appeal;
    }

    @Override
    @Transactional
    public void handleAppeal(Long id, Long handlerId, String status, String replyContent) {
        BizAppeal appeal = appealMapper.selectByIdForUpdate(id);
        if (appeal == null) {
            throw new BusinessException(404, "申诉记录不存在");
        }
        if (!"pending".equals(appeal.getStatus())) {
            throw new BusinessException(400, "该申诉已被处理");
        }

        appeal.setStatus(status);
        appeal.setReplyContent(replyContent);
        appeal.setHandlerId(handlerId);
        appeal.setHandleTime(LocalDateTime.now());
        appealMapper.updateById(appeal);

        // 更新违规记录申诉状态
        BizViolation violation = violationMapper.selectById(appeal.getViolationId());
        if (violation != null) {
            violation.setAppealStatus(status);
            violationMapper.updateById(violation);
        }
    }

    @Override
    public IPage<BizAppeal> getAllAppeals(String status, Integer page, Integer pageSize) {
        Page<BizAppeal> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<BizAppeal> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(BizAppeal::getStatus, status);
        }
        wrapper.orderByDesc(BizAppeal::getCreateTime);
        return appealMapper.selectPage(p, wrapper);
    }

    private String generateAppealNumber() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 1000);
        return "AP" + dateStr + String.format("%03d", random);
    }
}
