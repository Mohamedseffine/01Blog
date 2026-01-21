package com.zone01oujda.moblogging.admin.dto;

import java.time.LocalDateTime;

import com.zone01oujda.moblogging.report.enums.ReportReason;
import com.zone01oujda.moblogging.report.enums.ReportStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminReportDto {
    private Long id;
    private String contentType;
    private Long contentId;
    private ReportReason reason;
    private ReportStatus status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private String reporterUsername;
    private String reportedUsername;
}
