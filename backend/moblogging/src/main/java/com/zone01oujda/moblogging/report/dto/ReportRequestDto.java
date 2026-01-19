package com.zone01oujda.moblogging.report.dto;

import com.zone01oujda.moblogging.report.enums.ReportReason;
import com.zone01oujda.moblogging.report.enums.ReportType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for submitting a report
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDto {
    
    /**
     * Report type (POST, COMMENT, USER, etc)
     */
    @NotNull(message = "Report type is required")
    public ReportType reportType;
    
    /**
     * ID of the content being reported
     */
    @NotNull(message = "Content ID is required")
    public Long contentId;
    
    /**
     * Reason for the report
     */
    @NotNull(message = "Report reason is required")
    public ReportReason reason;
    
    /**
     * Additional details about the report
     */
    @NotBlank(message = "Report description is required")
    public String description;
}
