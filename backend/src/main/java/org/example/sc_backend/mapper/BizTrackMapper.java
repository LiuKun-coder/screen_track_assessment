package org.example.sc_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Mapper;
import org.example.sc_backend.entity.BizTrack;

@Mapper
public interface BizTrackMapper extends BaseMapper<BizTrack> {

	/**
	 * 统计近24小时活跃轨迹设备数
	 */
	@Select("SELECT COUNT(DISTINCT user_id) FROM biz_track WHERE user_id IS NOT NULL AND end_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR)")
	Integer countOnlineDevices();
}
