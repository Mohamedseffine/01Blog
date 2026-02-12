package com.zone01oujda.moblogging.admin.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zone01oujda.moblogging.admin.dto.AdminCommentDto;
import com.zone01oujda.moblogging.admin.dto.AdminDashboardDto;
import com.zone01oujda.moblogging.admin.dto.AdminPostDto;
import com.zone01oujda.moblogging.admin.dto.AdminReportDto;
import com.zone01oujda.moblogging.admin.dto.AdminUserDto;
import com.zone01oujda.moblogging.admin.dto.BanRequestDto;
import com.zone01oujda.moblogging.admin.repository.BanRepository;
import com.zone01oujda.moblogging.comment.repository.CommentRepository;
import com.zone01oujda.moblogging.entity.Ban;
import com.zone01oujda.moblogging.entity.Comment;
import com.zone01oujda.moblogging.entity.Post;
import com.zone01oujda.moblogging.entity.Report;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.AccessDeniedException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.post.enums.PostVisibility;
import com.zone01oujda.moblogging.post.repository.PostRepository;
import com.zone01oujda.moblogging.report.enums.ReportStatus;
import com.zone01oujda.moblogging.report.repository.ReportRepository;
import com.zone01oujda.moblogging.user.enums.Role;
import com.zone01oujda.moblogging.user.repository.UserRepository;
import com.zone01oujda.moblogging.util.SecurityUtil;

/**
 * Service class for admin operations
 */
@Service
public class AdminService {
    
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final BanRepository banRepository;

    public AdminService(UserRepository userRepository, PostRepository postRepository,
            CommentRepository commentRepository, ReportRepository reportRepository,
            BanRepository banRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.reportRepository = reportRepository;
        this.banRepository = banRepository;
    }

    /**
     * Get dashboard statistics
     * @return dashboard data
     */
    public AdminDashboardDto getDashboardStats() {
        return new AdminDashboardDto(
            userRepository.count(),
            postRepository.count(),
            commentRepository.count(),
            reportRepository.count(),
            userRepository.countByBannedTrue(),
            reportRepository.countByStatus(ReportStatus.PENDING)
        );
    }

    /**
     * Get paginated list of users
     * @param page page number
     * @param size page size
     * @return page of admin user DTOs
     */
    public Page<AdminUserDto> getAllUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size))
            .map(user -> new AdminUserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isBanned(),
                user.isBlocked(),
                user.getCreatedAt()
            ));
    }
    
    /**
     * Get paginated list of posts
     * @param page page number
     * @param size page size
     * @return page of admin post DTOs
     */
    public Page<AdminPostDto> getAllPosts(int page, int size, String sortDir,
            PostVisibility visibility,
            Boolean hidden, Long creatorId, String creatorUsername) {
        Sort sort = Sort.by(parseSortDir(sortDir), "createdAt");
        Page<AdminPostDto> base = postRepository
            .findForAdmin(visibility, hidden, creatorId, PageRequest.of(page, size, sort))
            .map(this::toAdminPostDto);

        if (creatorUsername == null || creatorUsername.isBlank()) {
            return base;
        }

        String needle = creatorUsername.toLowerCase();
        java.util.List<AdminPostDto> filtered = base.getContent().stream()
            .filter(dto -> dto.getCreatorUsername() != null
                && dto.getCreatorUsername().toLowerCase().contains(needle))
            .toList();
        return new PageImpl<>(filtered, base.getPageable(), filtered.size());
    }

    /**
     * Get paginated list of comments
     * @param page page number
     * @param size page size
     * @return page of admin comment DTOs
     */
    public Page<AdminCommentDto> getAllComments(int page, int size, String sortDir,
            Boolean hidden, Long postId, Long creatorId, String creatorUsername) {
        Sort sort = Sort.by(parseSortDir(sortDir), "createdAt");
        Page<AdminCommentDto> base = commentRepository
            .findForAdmin(hidden, postId, creatorId, PageRequest.of(page, size, sort))
            .map(this::toAdminCommentDto);

        if (creatorUsername == null || creatorUsername.isBlank()) {
            return base;
        }

        String needle = creatorUsername.toLowerCase();
        java.util.List<AdminCommentDto> filtered = base.getContent().stream()
            .filter(dto -> dto.getCreatorUsername() != null
                && dto.getCreatorUsername().toLowerCase().contains(needle))
            .toList();
        return new org.springframework.data.domain.PageImpl<>(filtered, base.getPageable(), filtered.size());
    }


    /**
     * Ban a user by ID
     * @param userId the user ID to ban
     * @throws ResourceNotFoundException if user not found
     */
    public void banUser(Long userId, BanRequestDto banRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User admin = getCurrentAdmin();
        if (user.getId().equals(admin.getId())) {
            throw new AccessDeniedException("You cannot ban yourself");
        }
        Ban ban = new Ban(user, admin, banRequest.getReason());
        boolean isPermanent = Boolean.TRUE.equals(banRequest.getIsPermanent());
        ban.setIsPermanent(isPermanent);
        if (!isPermanent && banRequest.getDurationDays() != null && banRequest.getDurationDays() > 0) {
            ban.setUnbannedAt(LocalDateTime.now().plusDays(banRequest.getDurationDays()));
        }
        user.setBanned(true);
        banRepository.save(ban);
        userRepository.save(user);
    }

    /**
     * Unban a user by ID
     * @param userId the user ID to unban
     * @throws ResourceNotFoundException if user not found
     */
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setBanned(false);
        userRepository.save(user);
        banRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
            .ifPresent(banRepository::delete);
    }

    /**
     * Delete post by ID
     * @param postId the post ID to delete
     */
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        reportRepository.deleteByPostId(postId);
        postRepository.delete(post);
    }

    public void hidePost(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        post.setHidden(true);
        postRepository.save(post);
    }

    public void unhidePost(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        post.setHidden(false);
        postRepository.save(post);
    }

    public void hideComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        comment.setHidden(true);
        commentRepository.save(comment);
    }

    public void unhideComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        comment.setHidden(false);
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        reportRepository.deleteByCommentId(commentId);
        commentRepository.delete(comment);
    }

    /**
     * Get paginated list of reports
     * @param page page number
     * @param size page size
     * @return page of admin report DTOs
     */
    public Page<AdminReportDto> getReports(int page, int size) {
        return reportRepository.findAll(PageRequest.of(page, size))
            .map(this::toAdminReportDto);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User target = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User current = getCurrentAdmin();
        if (target.getId().equals(current.getId())) {
            throw new AccessDeniedException("You cannot delete your own account");
        }
        banRepository.deleteByUserId(target.getId());
        reportRepository.deleteByReporterId(target.getId());
        reportRepository.deleteByReportedUserId(target.getId());
        // Reports, comments, posts, etc. are set to cascade/orphan removal; repo delete will cascade.
        userRepository.delete(target);
    }

    /**
     * Get system stats summary
     * @return stats map
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        Map<String, Long> reportsByStatus = new HashMap<>();
        for (ReportStatus status : ReportStatus.values()) {
            reportsByStatus.put(status.name(), reportRepository.countByStatus(status));
        }
        Map<String, Long> usersByRole = new HashMap<>();
        for (Role role : Role.values()) {
            usersByRole.put(role.name(), userRepository.countByRole(role));
        }
        stats.put("reportsByStatus", reportsByStatus);
        stats.put("usersByRole", usersByRole);
        return stats;
    }

    private User getCurrentAdmin() {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        return userRepository.findByUsernameOrEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));
    }

    private AdminReportDto toAdminReportDto(Report report) {
        String contentType = "UNKNOWN";
        Long contentId = null;
        String reportedUsername = null;
        if (report.getPost() != null) {
            contentType = "POST";
            contentId = report.getPost().getId();
            if (report.getPost().getCreator() != null) {
                reportedUsername = report.getPost().getCreator().getUsername();
            }
        } else if (report.getComment() != null) {
            contentType = "COMMENT";
            contentId = report.getComment().getId();
            if (report.getComment().getCreator() != null) {
                reportedUsername = report.getComment().getCreator().getUsername();
            }
        } else if (report.getReportedUser() != null) {
            contentType = "USER";
            contentId = report.getReportedUser().getId();
            reportedUsername = report.getReportedUser().getUsername();
        }

        return new AdminReportDto(
            report.getId(),
            contentType,
            contentId,
            report.getReason(),
            report.getStatus(),
            report.getDescription(),
            report.getCreatedAt(),
            report.getResolvedAt(),
            report.getReporter() != null ? report.getReporter().getUsername() : null,
            reportedUsername
        );
    }

    private AdminPostDto toAdminPostDto(Post post) {
        Long creatorId = null;
        String creatorUsername = null;
        if (post.getCreator() != null) {
            creatorId = post.getCreator().getId();
            creatorUsername = post.getCreator().getUsername();
        }
        return new AdminPostDto(
            post.getId(),
            post.getTitle(),
            post.getVisibility(),
            post.getCreatedAt(),
            post.getHidden(),
            creatorId,
            creatorUsername
        );
    }

    private AdminCommentDto toAdminCommentDto(Comment comment) {
        Long creatorId = null;
        String creatorUsername = null;
        if (comment.getCreator() != null) {
            creatorId = comment.getCreator().getId();
            creatorUsername = comment.getCreator().getUsername();
        }
        Long postId = comment.getPost() != null ? comment.getPost().getId() : null;
        return new AdminCommentDto(
            comment.getId(),
            comment.getContent(),
            postId,
            creatorId,
            creatorUsername,
            comment.getCreatedAt(),
            comment.getModifiedAt(),
            comment.getHidden()
        );
    }

    private Sort.Direction parseSortDir(String sortDir) {
        if (sortDir == null || sortDir.isBlank()) {
            return Sort.Direction.DESC;
        }
        try {
            return Sort.Direction.fromString(sortDir);
        } catch (IllegalArgumentException ex) {
            return Sort.Direction.DESC;
        }
    }
}
