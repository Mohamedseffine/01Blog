package com.zone01oujda.moblogging.react.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.react.dto.ReactDto;
import com.zone01oujda.moblogging.react.dto.ReactSummaryDto;
import com.zone01oujda.moblogging.react.service.ReactService;
import com.zone01oujda.moblogging.util.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reacts")
public class ReactController {

    private final ReactService reactService;

    public ReactController(ReactService reactService) {
        this.reactService = reactService;
    }

    @PostMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<ReactSummaryDto>> addPostReact(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody ReactDto reactDto) {
        ReactSummaryDto summary = reactService.reactToPost(postId, reactDto.reactType);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Reaction added successfully", summary));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<ReactSummaryDto>> removePostReact(@PathVariable("postId") Long postId) {
        ReactSummaryDto summary = reactService.removePostReaction(postId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reaction removed successfully", summary));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<ReactSummaryDto>> getPostReactSummary(@PathVariable("postId") Long postId) {
        ReactSummaryDto summary = reactService.getPostSummary(postId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reaction summary retrieved successfully", summary));
    }

    @PostMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<ReactSummaryDto>> addCommentReact(
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody ReactDto reactDto) {
        ReactSummaryDto summary = reactService.reactToComment(commentId, reactDto.reactType);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Reaction added successfully", summary));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<ReactSummaryDto>> removeCommentReact(@PathVariable("commentId") Long commentId) {
        ReactSummaryDto summary = reactService.removeCommentReaction(commentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reaction removed successfully", summary));
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<ReactSummaryDto>> getCommentReactSummary(@PathVariable("commentId") Long commentId) {
        ReactSummaryDto summary = reactService.getCommentSummary(commentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reaction summary retrieved successfully", summary));
    }
    
}
