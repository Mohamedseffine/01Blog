package com.zone01oujda.moblogging.react.service;

import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.entity.Comment;
import com.zone01oujda.moblogging.entity.CommentReact;
import com.zone01oujda.moblogging.entity.Post;
import com.zone01oujda.moblogging.entity.React;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.AccessDeniedException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.react.dto.ReactSummaryDto;
import com.zone01oujda.moblogging.react.enums.ReactType;
import com.zone01oujda.moblogging.react.repository.CommentReactRepository;
import com.zone01oujda.moblogging.react.repository.ReactRepository;
import com.zone01oujda.moblogging.comment.repository.CommentRepository;
import com.zone01oujda.moblogging.post.repository.PostRepository;
import com.zone01oujda.moblogging.user.repository.UserRepository;
import com.zone01oujda.moblogging.util.SecurityUtil;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReactService {

    private final ReactRepository reactRepository;
    private final CommentReactRepository commentReactRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public ReactService(
            ReactRepository reactRepository,
            CommentReactRepository commentReactRepository,
            UserRepository userRepository,
            PostRepository postRepository,
            CommentRepository commentRepository) {
        this.reactRepository = reactRepository;
        this.commentReactRepository = commentReactRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public ReactSummaryDto reactToPost(Long postId, ReactType type) {
        User user = getCurrentUser();
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        React react = reactRepository.findByUserIdAndPostId(user.getId(), postId).orElse(null);
        if (react == null) {
            react = new React(user, post, type);
        } else {
            react.setType(type);
        }
        reactRepository.save(react);

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
}
