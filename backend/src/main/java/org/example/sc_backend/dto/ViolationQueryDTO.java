package org.example.sc_backend.dto;

import lombok.Data;

/**
 * 违规查询DTO
 */
@Data
public class ViolationQueryDTO {
    /** 页码 */
    private Integer page = 1;

    /** 每页数量 */
    private Integer pageSize = 10;

    /** 违规类型筛选 */
    private String type;

    /** 状态筛选 */
    private String status;

    /** 申诉状态筛选 */
    private String appealStatus;

    /** 开始日期 */
    private String startDate;

    /** 结束日期 */
    private String endDate;

    /** 关键词搜索 */
    private String keyword;
}
