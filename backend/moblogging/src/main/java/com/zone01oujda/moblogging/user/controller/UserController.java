package com.zone01oujda.moblogging.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.util.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Users retrieved successfully", null)
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<Object>> getUserById(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "User retrieved successfully", null)
        );
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<Object>> updateUser(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody Object updateUserDto) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "User updated successfully", null)
        );
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "User deleted successfully")
        );
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponse<Object>> getFollowers(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Followers retrieved successfully", null)
        );
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<ApiResponse<Object>> getFollowing(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Following list retrieved successfully", null)
        );
    }
    
}
