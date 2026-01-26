package com.zone01oujda.moblogging.report.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.comment.repository.CommentRepository;
import com.zone01oujda.moblogging.entity.Comment;
import com.zone01oujda.moblogging.entity.Post;
import com.zone01oujda.moblogging.entity.Report;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.AccessDeniedException;
import com.zone01oujda.moblogging.exception.BadRequestException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.notification.enums.NotificationType;
import com.zone01oujda.moblogging.notification.service.NotificationService;
import com.zone01oujda.moblogging.post.repository.PostRepository;
import com.zone01oujda.moblogging.report.dto.CreateReportDto;
import com.zone01oujda.moblogging.report.enums.ReportStatus;
import com.zone01oujda.moblogging.report.repository.ReportRepository;
import com.zone01oujda.moblogging.user.enums.Role;
import com.zone01oujda.moblogging.user.repository.UserRepository;
import com.zone01oujda.moblogging.util.SecurityUtil;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;

    public ReportService(ReportRepository reportRepository, PostRepository postRepository, UserRepository userRepository,
            CommentRepository commentRepository, NotificationService notificationService) {
        this.reportRepository = reportRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
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
        
        // Notify all admins about the new report
        notifyAdminsAboutReport(report, reporter);
    }

    public void resolveReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        report.setStatus(ReportStatus.RESOLVED);
        report.setResolvedAt(LocalDateTime.now());
        reportRepository.save(report);
    }
    
    private void notifyAdminsAboutReport(Report report, User reporter) {
        List<User> admins = userRepository.findByRole(Role.ADMIN);
        
        String targetInfo = "unknown target";
        if (report.getPost() != null) {
            targetInfo = "post #" + report.getPost().getId();
        } else if (report.getComment() != null) {
            targetInfo = "comment #" + report.getComment().getId();
        } else if (report.getReportedUser() != null) {
            targetInfo = "user @" + report.getReportedUser().getUsername();
        }
        
        String notificationMessage = "New report from @" + reporter.getUsername() + " for " + targetInfo + 
                                      " (" + report.getReason() + ")";
        
        for (User admin : admins) {
            notificationService.createNotification(admin, NotificationType.REPORT, notificationMessage);
        }
    }
}
