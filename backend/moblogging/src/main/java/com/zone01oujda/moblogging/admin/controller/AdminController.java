package com.zone01oujda.moblogging.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.admin.dto.AdminCommentDto;
import com.zone01oujda.moblogging.admin.dto.AdminDashboardDto;
import com.zone01oujda.moblogging.admin.dto.AdminPostDto;
import com.zone01oujda.moblogging.admin.dto.AdminReportDto;
import com.zone01oujda.moblogging.admin.dto.AdminUserDto;
import com.zone01oujda.moblogging.admin.dto.BanRequestDto;
import com.zone01oujda.moblogging.admin.service.AdminService;
import com.zone01oujda.moblogging.post.enums.PostVisibility;
import com.zone01oujda.moblogging.util.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardDto>> getDashboard() {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Dashboard data retrieved successfully", adminService.getDashboardStats())
        );
    }
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<Page<AdminPostDto>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortDir,
            @RequestParam(required = false) PostVisibility visibility,
            @RequestParam(required = false) Boolean hidden,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) String creatorUsername) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Posts retrieved successfully",
                adminService.getAllPosts(page, size, sortDir, visibility, hidden, creatorId, creatorUsername))
        );
    }

    @GetMapping("/comments")
    public ResponseEntity<ApiResponse<Page<AdminCommentDto>>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortDir,
            @RequestParam(required = false) Boolean hidden,
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) String creatorUsername) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Comments retrieved successfully",
                adminService.getAllComments(page, size, sortDir, hidden, postId, creatorId, creatorUsername))
        );
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<AdminUserDto>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Users retrieved successfully", adminService.getAllUsers(page, size))
        );
    }

    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<ApiResponse<Void>> banUser(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody BanRequestDto banDto) {
        adminService.banUser(userId, banDto);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "User banned successfully")
        );
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("userId") Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully"));
    }

    @PostMapping("/users/{userId}/unban")
    public ResponseEntity<ApiResponse<Void>> unbanUser(@PathVariable("userId") Long userId) {
        adminService.unbanUser(userId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "User unbanned successfully")
        );
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable("postId") Long postId) {
        adminService.deletePost(postId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Post deleted successfully")
        );
    }

    @PostMapping("/posts/{postId}/hide")
    public ResponseEntity<ApiResponse<Void>> hidePost(@PathVariable("postId") Long postId) {
        adminService.hidePost(postId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Post hidden successfully"));
    }

    @PostMapping("/posts/{postId}/unhide")
    public ResponseEntity<ApiResponse<Void>> unhidePost(@PathVariable("postId") Long postId) {
        adminService.unhidePost(postId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Post unhidden successfully"));
    }

    @PostMapping("/comments/{commentId}/hide")
    public ResponseEntity<ApiResponse<Void>> hideComment(@PathVariable("commentId") Long commentId) {
        adminService.hideComment(commentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Comment hidden successfully"));
    }

    @PostMapping("/comments/{commentId}/unhide")
    public ResponseEntity<ApiResponse<Void>> unhideComment(@PathVariable("commentId") Long commentId) {
        adminService.unhideComment(commentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Comment unhidden successfully"));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable("commentId") Long commentId) {
        adminService.deleteComment(commentId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Comment deleted successfully"));
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<Page<AdminReportDto>>> getReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Reports retrieved successfully", adminService.getReports(page, size))
        );
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getStats() {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Statistics retrieved successfully", adminService.getStats())
        );
    }
    
}
