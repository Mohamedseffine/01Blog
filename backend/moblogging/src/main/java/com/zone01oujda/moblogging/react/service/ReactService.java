package com.zone01oujda.moblogging.react.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zone01oujda.moblogging.comment.repository.CommentRepository;
import com.zone01oujda.moblogging.entity.Comment;
import com.zone01oujda.moblogging.entity.CommentReact;
import com.zone01oujda.moblogging.entity.Post;
import com.zone01oujda.moblogging.entity.React;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.AccessDeniedException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.notification.enums.NotificationType;
import com.zone01oujda.moblogging.notification.service.NotificationService;
import com.zone01oujda.moblogging.post.repository.PostRepository;
import com.zone01oujda.moblogging.react.dto.ReactSummaryDto;
import com.zone01oujda.moblogging.react.enums.ReactType;
import com.zone01oujda.moblogging.react.repository.CommentReactRepository;
import com.zone01oujda.moblogging.react.repository.ReactRepository;
import com.zone01oujda.moblogging.user.repository.UserRepository;
import com.zone01oujda.moblogging.util.SecurityUtil;

@Service
public class ReactService {

    private final ReactRepository reactRepository;
    private final CommentReactRepository commentReactRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;

    public ReactService(
            ReactRepository reactRepository,
            CommentReactRepository commentReactRepository,
            UserRepository userRepository,
            PostRepository postRepository,
            CommentRepository commentRepository,
            NotificationService notificationService) {
        this.reactRepository = reactRepository;
        this.commentReactRepository = commentReactRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
    }

    public ReactSummaryDto reactToPost(Long postId, ReactType type) {
        User user = getCurrentUser();
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        ensurePostInteractable(post);

        React react = reactRepository.findByUserIdAndPostId(user.getId(), postId).orElse(null);
        ReactType previousType = react != null ? react.getType() : null;
        if (react == null) {
            react = new React(user, post, type);
        } else {
            react.setType(type);
        }
        reactRepository.save(react);

        User owner = post.getCreator();
        if (owner != null && !owner.getId().equals(user.getId())
                && (previousType == null || previousType != type)) {
            notificationService.createNotification(
                owner,
                NotificationType.REACT,
                user.getUsername() + " reacted to your post"
            );
        }

        return getPostSummary(postId, user.getId());
    }

    @Transactional
    public ReactSummaryDto removePostReaction(Long postId) {
        User user = getCurrentUser();
        reactRepository.findByUserIdAndPostId(user.getId(), postId)
            .ifPresent(reactRepository::delete);
        return getPostSummary(postId, user.getId());
    }

    public ReactSummaryDto getPostSummary(Long postId) {
        User user = getCurrentUser();
        return getPostSummary(postId, user.getId());
    }

    private ReactSummaryDto getPostSummary(Long postId, Long userId) {
        long likes = reactRepository.countByPostIdAndType(postId, ReactType.LIKE);
        long dislikes = reactRepository.countByPostIdAndType(postId, ReactType.DISLIKE);
        ReactType userReact = reactRepository.findByUserIdAndPostId(userId, postId)
            .map(React::getType)
            .orElse(null);
        return new ReactSummaryDto(likes, dislikes, userReact);
    }

    public ReactSummaryDto reactToComment(Long commentId, ReactType type) {
        User user = getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        ensurePostInteractable(comment.getPost());

        CommentReact react = commentReactRepository.findByUserIdAndCommentId(user.getId(), commentId).orElse(null);
        if (react == null) {
            react = new CommentReact(user, comment, type);
        } else {
            react.setType(type);
        }
        commentReactRepository.save(react);

        return getCommentSummary(commentId, user.getId());
    }

    @Transactional
    public ReactSummaryDto removeCommentReaction(Long commentId) {
        User user = getCurrentUser();
        commentReactRepository.findByUserIdAndCommentId(user.getId(), commentId)
            .ifPresent(commentReactRepository::delete);
        return getCommentSummary(commentId, user.getId());
    }

    public ReactSummaryDto getCommentSummary(Long commentId) {
        User user = getCurrentUser();
        return getCommentSummary(commentId, user.getId());
    }

    private ReactSummaryDto getCommentSummary(Long commentId, Long userId) {
        long likes = commentReactRepository.countByCommentIdAndType(commentId, ReactType.LIKE);
        long dislikes = commentReactRepository.countByCommentIdAndType(commentId, ReactType.DISLIKE);
        ReactType userReact = commentReactRepository.findByUserIdAndCommentId(userId, commentId)
            .map(CommentReact::getType)
            .orElse(null);
        return new ReactSummaryDto(likes, dislikes, userReact);
    }

    private User getCurrentUser() {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        return userRepository.findByUsernameOrEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void ensurePostInteractable(Post post) {
        if (post == null) {
            throw new ResourceNotFoundException("Post not found");
        }
        if (Boolean.TRUE.equals(post.getHidden()) && !SecurityUtil.hasRole("ADMIN")) {
            throw new ResourceNotFoundException("Post not found");
        }
    }
}
