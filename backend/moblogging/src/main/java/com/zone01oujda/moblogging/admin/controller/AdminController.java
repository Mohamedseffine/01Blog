package com.zone01oujda.moblogging.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.util.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Object>> getDashboard() {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Dashboard data retrieved successfully", null)
        );
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Users retrieved successfully", null)
        );
    }

    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<ApiResponse<Void>> banUser(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody Object banDto) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "User banned successfully")
        );
    }

    @PostMapping("/users/{userId}/unban")
    public ResponseEntity<ApiResponse<Void>> unbanUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "User unbanned successfully")
        );
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable("postId") Long postId) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Post deleted successfully")
        );
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<Object>> getReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Reports retrieved successfully", null)
        );
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getStats() {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Statistics retrieved successfully", null)
        );
    }
    
}
