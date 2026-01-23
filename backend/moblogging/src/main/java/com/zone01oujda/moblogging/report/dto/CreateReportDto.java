package com.zone01oujda.moblogging.report.dto;

import com.zone01oujda.moblogging.report.enums.ReportReason;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReportDto {
    private Long postId;

    private Long commentId;

    private Long reportedUserId;

    @NotNull(message = "Report reason is required")
    private ReportReason reason;

    private String description;
}
