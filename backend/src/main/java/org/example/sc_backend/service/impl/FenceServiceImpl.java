package org.example.sc_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.sc_backend.common.exception.BusinessException;
import org.example.sc_backend.entity.BizFence;
import org.example.sc_backend.mapper.BizFenceMapper;
import org.example.sc_backend.service.FenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 电子围栏服务实现类
 */
@Service
public class FenceServiceImpl implements FenceService {

    @Autowired
    private BizFenceMapper fenceMapper;

    @Override
    public List<BizFence> getAllFences() {
        return fenceMapper.selectList(
                new LambdaQueryWrapper<BizFence>().eq(BizFence::getStatus, "active")
        );
    }

    @Override
    public BizFence getFenceDetail(Long id) {
        BizFence fence = fenceMapper.selectById(id);
        if (fence == null) {
            throw new BusinessException(404, "围栏不存在");
        }
        return fence;
    }

    @Override
    public void addFence(BizFence fence) {
        fence.setStatus("active");
        fenceMapper.insert(fence);
    }

    @Override
    public void updateFence(BizFence fence) {
        fenceMapper.updateById(fence);
    }

    @Override
    public void deleteFence(Long id) {
        BizFence fence = fenceMapper.selectById(id);
        if (fence != null) {
            fence.setStatus("deleted");
            fenceMapper.updateById(fence);
        }
    }
}
