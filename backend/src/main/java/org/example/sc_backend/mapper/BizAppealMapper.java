package org.example.sc_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.example.sc_backend.entity.BizAppeal;

@Mapper
public interface BizAppealMapper extends BaseMapper<BizAppeal> {
    @Select("SELECT * FROM biz_appeal WHERE id = #{id} FOR UPDATE")
    BizAppeal selectByIdForUpdate(@Param("id") Long id);
}
