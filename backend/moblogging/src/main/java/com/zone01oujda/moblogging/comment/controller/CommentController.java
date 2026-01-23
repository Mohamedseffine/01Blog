package com.zone01oujda.moblogging.comment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.comment.dto.CommentDto;
import com.zone01oujda.moblogging.comment.dto.CreateCommentDto;
import com.zone01oujda.moblogging.comment.service.CommentService;
import com.zone01oujda.moblogging.util.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/comments")
public class CommentController {
    
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommentDto>> createComment(@Valid @RequestBody CreateCommentDto createCommentDto) {
        CommentDto comment = commentService.create(createCommentDto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Comment created successfully", comment));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDto>> getComment(@PathVariable("commentId") Long commentId) {
        CommentDto comment = commentService.getComments(commentId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Comment retrieved successfully", comment)
        );
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<CommentDto>>> getByPost(
            @PathVariable("postId") Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Comments retrieved successfully",
                commentService.getCommentsByPost(postId, page, size))
        );
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDto>> updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestBody CreateCommentDto updateDto) {
        CommentDto comment = commentService.updateComment(commentId, updateDto.content);
        return ResponseEntity.ok(new ApiResponse<>(true, "Comment updated successfully", comment));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Comment deleted successfully"));
    }
    
}
