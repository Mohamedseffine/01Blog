package com.zone01oujda.moblogging.report.dto;

import com.zone01oujda.moblogging.report.enums.ReportReason;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReportDto {
    @NotNull(message = "Post ID is required")
    private Long postId;

    @NotNull(message = "Report reason is required")
    private ReportReason reason;

    private String description;
}
