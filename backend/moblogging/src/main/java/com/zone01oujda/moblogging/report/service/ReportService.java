package com.zone01oujda.moblogging.report.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.entity.Report;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.report.enums.ReportStatus;
import com.zone01oujda.moblogging.report.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public void resolveReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        report.setStatus(ReportStatus.RESOLVED);
        report.setResolvedAt(LocalDateTime.now());
        reportRepository.save(report);
    }
}
