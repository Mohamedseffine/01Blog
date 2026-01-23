package com.zone01oujda.moblogging.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;
import java.nio.file.Files;
import java.nio.file.Path;

import com.zone01oujda.moblogging.user.dto.UserDto;
import com.zone01oujda.moblogging.user.dto.UpdateUserDto;
import com.zone01oujda.moblogging.user.service.UserService;
import com.zone01oujda.moblogging.util.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Users retrieved successfully", null)
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable("userId") Long userId) {
        UserDto user = userService.getUserById(userId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "User retrieved successfully", user)
        );
    }

    @GetMapping("/{userId}/profile-picture")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable("userId") Long userId) {
        Resource resource = userService.getProfilePicture(userId);
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

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        UserDto user = userService.getCurrentUser();
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Current user retrieved successfully", user)
        );
    }

    @PutMapping(value = "/{userId}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> updateUser(
            @PathVariable("userId") Long userId,
            @Valid @ModelAttribute UpdateUserDto updateUserDto) {
        UserDto updated = userService.updateUser(userId, updateUserDto);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "User updated successfully", updated)
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
