package com.zone01oujda.moblogging.report.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.entity.Post;
import com.zone01oujda.moblogging.entity.Report;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.AccessDeniedException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.report.enums.ReportStatus;
import com.zone01oujda.moblogging.report.dto.CreateReportDto;
import com.zone01oujda.moblogging.report.repository.ReportRepository;
import com.zone01oujda.moblogging.post.repository.PostRepository;
import com.zone01oujda.moblogging.user.repository.UserRepository;
import com.zone01oujda.moblogging.util.SecurityUtil;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository, PostRepository postRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public void createReport(CreateReportDto dto) {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        if (SecurityUtil.hasRole("ADMIN")) {
            throw new AccessDeniedException("Admins cannot submit reports");
        }

        User reporter = userRepository.findByUsernameOrEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Post post = postRepository.findById(dto.getPostId())
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Report report = new Report(reporter, dto.getReason(), dto.getDescription());
        report.setPost(post);
        reportRepository.save(report);
    }

    public void resolveReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        report.setStatus(ReportStatus.RESOLVED);
        report.setResolvedAt(LocalDateTime.now());
        reportRepository.save(report);
    }
}
