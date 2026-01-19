package com.zone01oujda.moblogging.post.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.post.dto.CreatePostDto;
import com.zone01oujda.moblogging.post.dto.PostDto;
import com.zone01oujda.moblogging.post.service.PostService;
import com.zone01oujda.moblogging.util.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<PostDto>> createPost(@Valid @ModelAttribute CreatePostDto createPostDto) {
        PostDto post = postService.createPost(createPostDto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Post created successfully", post));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDto>> getPostById(@PathVariable("postId") Long postId) {
        PostDto post = postService.getPostById(postId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Post retrieved successfully", post)
        );
    }
    
}
