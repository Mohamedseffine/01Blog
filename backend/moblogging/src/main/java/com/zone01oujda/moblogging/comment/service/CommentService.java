package com.zone01oujda.moblogging.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.comment.dto.CommentDto;
import com.zone01oujda.moblogging.comment.dto.CreateCommentDto;
import com.zone01oujda.moblogging.comment.repository.CommentRepository;
import com.zone01oujda.moblogging.comment.util.CommentMapper;
import com.zone01oujda.moblogging.entity.Comment;
import com.zone01oujda.moblogging.entity.Post;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.AccessDeniedException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.post.repository.PostRepository;
import com.zone01oujda.moblogging.user.repository.UserRepository;
import com.zone01oujda.moblogging.util.SecurityUtil;

/**
 * Service class for comment operations
 */
@Service
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public CommentService(UserRepository userRepository, PostRepository postRepository,
            CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
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

        // Create and save comment
        Comment comment = new Comment(dto.content, parent);
        comment.setCreator(user);
        comment.setPost(post);
        comment = commentRepository.save(comment);

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

        return convertToDto(comment);
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
            comment.getPost().getId()
        );

        // Add child comments
        List<CommentDto> childDtos = comment.getChildren().stream()
                .map(CommentMapper::toDto)
                .toList();
        dto.setChildren(childDtos);

        return dto;
    }
}
