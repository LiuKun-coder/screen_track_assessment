package org.example.sc_backend.dto;

import lombok.Data;

/**
 * 用户查询DTO
 */
@Data
public class UserQueryDTO {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String keyword;
    private String userType;
    private String status;
}
