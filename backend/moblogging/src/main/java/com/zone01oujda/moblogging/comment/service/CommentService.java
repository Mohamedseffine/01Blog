package com.zone01oujda.moblogging.comment.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.comment.dto.CommentDto;
import com.zone01oujda.moblogging.comment.dto.CreateCommentDto;
import com.zone01oujda.moblogging.comment.repository.CommentRepository;
import com.zone01oujda.moblogging.comment.util.CommentMapper;
import com.zone01oujda.moblogging.entity.Comment;
import com.zone01oujda.moblogging.entity.Post;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.post.repository.PostRepository;
import com.zone01oujda.moblogging.user.repository.UserRepository;

@Service
public class CommentService {

    private UserRepository userRepository;
    private PostRepository postRepository;
    private CommentRepository commentRepository;

    public CommentService(UserRepository userRepository, PostRepository postRepository,
            CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public CommentDto create(CreateCommentDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsernameOrEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("the user not found"));
        Comment parent = null;
        if (dto.getParentId() != 0) {
            parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Comment not found"));
        }
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("Cannot find post"));
        Comment comment = new Comment(dto.getContent(), parent);
        comment.setCreator(user);
        comment.setPost(post);
        comment = commentRepository.save(comment);
        return new CommentDto(comment.getId(), comment.getParent() != null ? comment.getParent().getId() : 0, comment.getContent(), comment.getHidden(),
                comment.getCreatedAt(), comment.getModifiedAt(), comment.getModified(), comment.getPost().getId());
    }

    public CommentDto getComments(Long postId) {
        Comment comment = commentRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post Not Found"));
        CommentDto dto = new CommentDto(comment.getId(), comment.getParent() != null ? comment.getParent().getId() : 0, comment.getContent(),
                comment.getHidden(), comment.getCreatedAt(), comment.getModifiedAt(), comment.getModified(),
                comment.getPost().getId());

        List<CommentDto> dtos = comment.getChildren().stream()
                .map(CommentMapper::toDto)
                .toList();
        dto.setChildren(dtos);
        return dto;
    }

}
