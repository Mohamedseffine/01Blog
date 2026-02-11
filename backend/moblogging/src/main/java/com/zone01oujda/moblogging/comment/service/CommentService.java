package com.zone01oujda.moblogging.comment.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zone01oujda.moblogging.comment.dto.CommentDto;
import com.zone01oujda.moblogging.comment.dto.CreateCommentDto;
import com.zone01oujda.moblogging.comment.repository.CommentRepository;
import com.zone01oujda.moblogging.comment.util.CommentMapper;
import com.zone01oujda.moblogging.entity.Comment;
import com.zone01oujda.moblogging.entity.Post;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.AccessDeniedException;
import com.zone01oujda.moblogging.exception.BadRequestException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.post.repository.PostRepository;
import com.zone01oujda.moblogging.user.repository.UserRepository;
import com.zone01oujda.moblogging.notification.enums.NotificationType;
import com.zone01oujda.moblogging.notification.service.NotificationService;
import com.zone01oujda.moblogging.util.SecurityUtil;
import com.zone01oujda.moblogging.report.repository.ReportRepository;

/**
 * Service class for comment operations
 */
@Service
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;
    private final ReportRepository reportRepository;

    public CommentService(UserRepository userRepository, PostRepository postRepository,
            CommentRepository commentRepository, NotificationService notificationService, ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
        this.reportRepository = reportRepository;
    }

    /**
     * Create a new comment on a post
     * @param dto the comment creation data
     * @return CommentDto with created comment details
     * @throws AccessDeniedException if user is not authenticated
     * @throws ResourceNotFoundException if post or parent comment not found
     */
    public CommentDto create(CreateCommentDto dto) {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        // Get authenticated user
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get parent comment if replying to another comment
        Comment parent = null;
        if (dto.parentId != null && dto.parentId != 0) {
            parent = commentRepository.findById(dto.parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
        }

        // Get post that comment belongs to
        Post post = postRepository.findById(dto.postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (Boolean.TRUE.equals(post.getHidden()) && !SecurityUtil.hasRole("ADMIN")) {
            throw new ResourceNotFoundException("Post not found");
        }

        // Create and save comment
        String content = trimToNull(dto.content);
        if (content == null) {
            throw new BadRequestException("Comment content is required");
        }

        Comment comment = new Comment(content, parent);
        comment.setCreator(user);
        comment.setPost(post);
        comment = commentRepository.save(comment);

        User postOwner = post.getCreator();
        if (postOwner != null && !postOwner.getId().equals(user.getId())) {
            notificationService.createNotification(
                postOwner,
                NotificationType.COMMENT,
                user.getUsername() + " commented on your post"
            );
        }

        return convertToDto(comment);
    }

    /**
     * Get comment by ID with child comments
     * @param commentId the comment ID
     * @return CommentDto with comment details and child comments
     * @throws ResourceNotFoundException if comment not found
     */
    public CommentDto getComments(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        ensurePostCommentsVisible(comment.getPost());
        return convertToDto(comment);
    }

    public Page<CommentDto> getCommentsByPost(Long postId, int page, int size) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        ensurePostCommentsVisible(post);

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        if (SecurityUtil.hasRole("ADMIN")) {
            return commentRepository.findByPostId(postId, pageable).map(this::convertToDto);
        }
        return commentRepository.findByPostIdAndHiddenFalse(postId, pageable)
                .map(this::convertToDto);
    }

    public CommentDto updateComment(Long commentId, String content) {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (comment.getCreator() == null || !username.equals(comment.getCreator().getUsername())) {
            throw new AccessDeniedException("You cannot update this comment");
        }

        String newContent = trimToNull(content);
        if (newContent == null) {
            throw new BadRequestException("Comment content is required");
        }

        comment.setContent(newContent);
        comment.setModified(true);
        comment.setModifiedAt(java.time.LocalDateTime.now());
        Comment saved = commentRepository.save(comment);
        return convertToDto(saved);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (comment.getCreator() == null || !username.equals(comment.getCreator().getUsername())) {
            throw new AccessDeniedException("You cannot delete this comment");
        }

        reportRepository.deleteByCommentId(commentId);
        commentRepository.delete(comment);
    }

    /**
     * Convert Comment entity to CommentDto
     * @param comment the comment entity
     * @return CommentDto
     */
    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto(
            comment.getId(),
            comment.getParent() != null ? comment.getParent().getId() : 0,
            comment.getContent(),
            comment.getHidden(),
            comment.getCreatedAt(),
            comment.getModifiedAt(),
            comment.getModified(),
            comment.getPost().getId(),
            comment.getCreator() != null ? comment.getCreator().getId() : null,
            comment.getCreator() != null ? comment.getCreator().getUsername() : null
        );

        // Add child comments
        List<CommentDto> childDtos = (comment.getChildren() == null)
            ? List.of()
            : comment.getChildren().stream().map(CommentMapper::toDto).toList();
        dto.setChildren(childDtos);

        return dto;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void ensurePostCommentsVisible(Post post) {
        if (!Boolean.TRUE.equals(post.getHidden())) {
            return;
        }

        boolean isAdmin = SecurityUtil.hasRole("ADMIN");
        String username = SecurityUtil.getCurrentUsername();
        boolean isPostCreator = username != null
            && post.getCreator() != null
            && username.equals(post.getCreator().getUsername());

        if (!isAdmin && !isPostCreator) {
            throw new ResourceNotFoundException("Post not found");
        }
    }
}
