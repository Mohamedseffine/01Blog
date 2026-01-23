package com.zone01oujda.moblogging.report.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.entity.Post;
import com.zone01oujda.moblogging.entity.Report;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.entity.Comment;
import com.zone01oujda.moblogging.exception.AccessDeniedException;
import com.zone01oujda.moblogging.exception.BadRequestException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.report.enums.ReportStatus;
import com.zone01oujda.moblogging.report.dto.CreateReportDto;
import com.zone01oujda.moblogging.report.repository.ReportRepository;
import com.zone01oujda.moblogging.post.repository.PostRepository;
import com.zone01oujda.moblogging.user.repository.UserRepository;
import com.zone01oujda.moblogging.util.SecurityUtil;
import com.zone01oujda.moblogging.comment.repository.CommentRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public ReportService(ReportRepository reportRepository, PostRepository postRepository, UserRepository userRepository,
            CommentRepository commentRepository) {
        this.reportRepository = reportRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public void createReport(CreateReportDto dto) {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        if (SecurityUtil.hasRole("ADMIN")) {
            throw new AccessDeniedException("Admins cannot submit reports");
        }

        int targets = 0;
        if (dto.getPostId() != null) targets++;
        if (dto.getCommentId() != null) targets++;
        if (dto.getReportedUserId() != null) targets++;
        if (targets != 1) {
            throw new BadRequestException("Exactly one target is required");
        }

        User reporter = userRepository.findByUsernameOrEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Report report = new Report(reporter, dto.getReason(), dto.getDescription());
        if (dto.getPostId() != null) {
            Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
            report.setPost(post);
        } else if (dto.getCommentId() != null) {
            Comment comment = commentRepository.findById(dto.getCommentId())
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
            if (comment.getCreator() != null && reporter.getId().equals(comment.getCreator().getId())) {
                throw new BadRequestException("You cannot report your own comment");
            }
            report.setComment(comment);
        } else if (dto.getReportedUserId() != null) {
            if (reporter.getId().equals(dto.getReportedUserId())) {
                throw new BadRequestException("You cannot report yourself");
            }
            User reportedUser = userRepository.findById(dto.getReportedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            report.setReportedUser(reportedUser);
        }
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
