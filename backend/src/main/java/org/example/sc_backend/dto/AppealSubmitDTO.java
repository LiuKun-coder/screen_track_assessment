package org.example.sc_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

/**
 * 申诉提交DTO
 */
@Data
public class AppealSubmitDTO {
    @NotNull(message = "违规ID不能为空")
    private Long violationId;

    @NotBlank(message = "申诉原因不能为空")
    private String reason;

    private String description;

    private String contactPhone;

    /** 证据文件URL列表 */
    private List<String> evidenceFiles;

    /** 来源平台: web, miniapp */
    private String platform = "web";
}
