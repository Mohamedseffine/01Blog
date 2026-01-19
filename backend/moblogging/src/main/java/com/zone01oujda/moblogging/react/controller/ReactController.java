package com.zone01oujda.moblogging.react.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.util.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reacts")
public class ReactController {

    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<Object>> addReact(
            @PathVariable("postId") Long postId,
            @Valid @RequestBody Object reactDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Reaction added successfully", null));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> removeReact(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Reaction removed successfully")
        );
    }

    @PostMapping("/{postId}/count")
    public ResponseEntity<ApiResponse<Object>> getReactCount(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Reaction count retrieved successfully", null)
        );
    }
    
}
