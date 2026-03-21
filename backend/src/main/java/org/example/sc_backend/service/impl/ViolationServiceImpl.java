package org.example.sc_backend.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.sc_backend.common.exception.BusinessException;
import org.example.sc_backend.dto.ViolationQueryDTO;
import org.example.sc_backend.dto.ViolationVO;
import org.example.sc_backend.entity.BizViolation;
import org.example.sc_backend.mapper.BizViolationMapper;
import org.example.sc_backend.service.ViolationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 违规管理服务实现类
 */
@Service
public class ViolationServiceImpl implements ViolationService {

    @Autowired
    private BizViolationMapper violationMapper;

    @Override
    public IPage<ViolationVO> getViolationPage(ViolationQueryDTO queryDTO) {
        Page<BizViolation> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());

        LambdaQueryWrapper<BizViolation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizViolation::getDeleted, 0);

        // 动态条件
        if (StringUtils.hasText(queryDTO.getType())) {
            wrapper.eq(BizViolation::getType, queryDTO.getType());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(BizViolation::getStatus, queryDTO.getStatus());
        }
        if (StringUtils.hasText(queryDTO.getAppealStatus())) {
            wrapper.eq(BizViolation::getAppealStatus, queryDTO.getAppealStatus());
        }
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w
                    .like(BizViolation::getUserName, queryDTO.getKeyword())
                    .or()
                    .like(BizViolation::getPlace, queryDTO.getKeyword())
            );
        }
        if (StringUtils.hasText(queryDTO.getStartDate())) {
            wrapper.ge(BizViolation::getViolationTime, queryDTO.getStartDate());
        }
        if (StringUtils.hasText(queryDTO.getEndDate())) {
            wrapper.le(BizViolation::getViolationTime, queryDTO.getEndDate());
        }

        wrapper.orderByDesc(BizViolation::getCreateTime);

        IPage<BizViolation> resultPage = violationMapper.selectPage(page, wrapper);

        return resultPage.convert(this::toVO);
    }

    @Override
    public IPage<ViolationVO> getMyViolations(Long userId, Integer page, Integer pageSize,
                                              String type, String status,
                                              LocalDateTime startTime, LocalDateTime endTime) {
        Page<BizViolation> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<BizViolation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizViolation::getUserId, userId)
                .eq(BizViolation::getDeleted, 0);

        if (StringUtils.hasText(type)) {
            wrapper.eq(BizViolation::getType, type);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(BizViolation::getStatus, status);
        }
        if (startTime != null) {
            wrapper.ge(BizViolation::getViolationTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(BizViolation::getViolationTime, endTime);
        }

        wrapper.orderByDesc(BizViolation::getCreateTime);

        IPage<BizViolation> resultPage = violationMapper.selectPage(p, wrapper);
        return resultPage.convert(this::toVO);
    }

    @Override
    public ViolationVO getViolationDetail(Long id) {
        BizViolation violation = violationMapper.selectById(id);
        if (violation == null || violation.getDeleted() == 1) {
            throw new BusinessException(404, "违规记录不存在");
        }
        return toVO(violation);
    }

    @Override
    public void addViolation(BizViolation violation) {
        violation.setStatus("processed");
        violation.setAppealStatus("none");
        violation.setCanAppeal(1);
        violationMapper.insert(violation);
    }

    @Override
    public void updateViolation(BizViolation violation) {
        violationMapper.updateById(violation);
    }

    @Override
    public Map<String, Object> getStatistics(Long userId) {
        LambdaQueryWrapper<BizViolation> baseWrapper = new LambdaQueryWrapper<>();
        baseWrapper.eq(BizViolation::getDeleted, 0);

        Long totalCount = violationMapper.selectCount(baseWrapper);

        Long myCount = 0L;
        if (userId != null) {
            myCount = violationMapper.selectCount(
                    new LambdaQueryWrapper<BizViolation>()
                            .eq(BizViolation::getDeleted, 0)
                            .eq(BizViolation::getUserId, userId)
            );
        }

        Long pendingCount = violationMapper.selectCount(
                new LambdaQueryWrapper<BizViolation>()
                        .eq(BizViolation::getDeleted, 0)
                        .eq(BizViolation::getStatus, "pending")
        );

        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", totalCount);
        result.put("myCount", myCount);
        result.put("pendingCount", pendingCount);
        return result;
    }

    private ViolationVO toVO(BizViolation entity) {
        ViolationVO vo = new ViolationVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setCanAppeal(entity.getCanAppeal() != null && entity.getCanAppeal() == 1);
        // JSON字符串转List
        if (StringUtils.hasText(entity.getEvidenceImages())) {
            vo.setEvidenceImages(JSON.parseArray(entity.getEvidenceImages(), String.class));
        }
        return vo;
    }
}
