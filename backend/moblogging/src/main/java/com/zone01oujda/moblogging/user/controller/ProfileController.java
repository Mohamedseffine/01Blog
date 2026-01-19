package com.zone01oujda.moblogging.user.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.util.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getCurrentUserProfile() {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Profile retrieved successfully", null)
        );
    }

    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<Object>> updateProfile(
            @Valid @ModelAttribute Object updateProfileDto) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Profile updated successfully", null)
        );
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getProfileStats() {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Profile stats retrieved successfully", null)
        );
    }
    
}
