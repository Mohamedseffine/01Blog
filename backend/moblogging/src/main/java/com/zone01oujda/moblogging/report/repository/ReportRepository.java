package com.zone01oujda.moblogging.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zone01oujda.moblogging.entity.Report;
import com.zone01oujda.moblogging.report.enums.ReportStatus;

public interface ReportRepository extends JpaRepository<Report, Long> {
    long countByStatus(ReportStatus status);
}
