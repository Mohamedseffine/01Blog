package com.zone01oujda.moblogging.post.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.post.dto.CreatePostDto;
import com.zone01oujda.moblogging.post.dto.PostDto;
import com.zone01oujda.moblogging.post.dto.UpdatePostDto;
import com.zone01oujda.moblogging.post.service.PostService;
import com.zone01oujda.moblogging.util.response.ApiResponse;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.core.io.Resource;
import java.nio.file.Files;
import java.nio.file.Path;

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

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostDto>>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostDto> posts = postService.getPosts(page, size);
        return ResponseEntity.ok(new ApiResponse<>(true, "Posts retrieved successfully", posts));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<PostDto>>> getPostsByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostDto> posts = postService.getUserPosts(userId, page, size);
        return ResponseEntity.ok(new ApiResponse<>(true, "User posts retrieved successfully", posts));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDto>> getPostById(@PathVariable("postId") Long postId) {
        PostDto post = postService.getPostById(postId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Post retrieved successfully", post)
        );
    }

    @PutMapping(value = "/{postId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ApiResponse<PostDto>> updatePost(
            @PathVariable("postId") Long postId,
            @Valid @org.springframework.web.bind.annotation.ModelAttribute UpdatePostDto updatePostDto) {
        PostDto post = postService.updatePost(postId, updatePostDto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Post updated successfully", post));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable("postId") Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Post deleted successfully"));
    }

    @GetMapping("/{postId}/media/{index}")
    public ResponseEntity<Resource> getPostMedia(
            @PathVariable("postId") Long postId,
            @PathVariable("index") int index) {
        Resource resource = postService.getPostMedia(postId, index);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        try {
            Path path = Path.of(resource.getFile().getAbsolutePath());
            String detected = Files.probeContentType(path);
            if (detected != null) {
                mediaType = MediaType.parseMediaType(detected);
            }
        } catch (Exception ignored) {
            // fallback to octet-stream
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
    
}
