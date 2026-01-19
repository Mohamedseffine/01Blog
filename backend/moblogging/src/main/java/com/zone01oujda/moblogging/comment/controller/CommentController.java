package com.zone01oujda.moblogging.comment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    
}
