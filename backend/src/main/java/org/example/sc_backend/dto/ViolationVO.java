package org.example.sc_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 违规记录VO (返回前端)
 */
@Data
public class ViolationVO {
    private Long id;
    private Long userId;
    private String userName;
    private LocalDateTime violationTime;
    private String place;
    private String type;
    private String detail;
    private String penalty;
    private String status;
    private List<String> evidenceImages;
    private Boolean canAppeal;
    private String appealStatus;
    private LocalDateTime createTime;
}
