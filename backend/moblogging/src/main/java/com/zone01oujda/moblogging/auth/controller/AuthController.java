package com.zone01oujda.moblogging.auth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.auth.dto.AuthResponseDto;
import com.zone01oujda.moblogging.auth.dto.AuthTokensDto;
import com.zone01oujda.moblogging.auth.dto.CurrentUserDto;
import com.zone01oujda.moblogging.auth.dto.LoginRequestDto;
import com.zone01oujda.moblogging.auth.dto.RegisterRequestDto;
import com.zone01oujda.moblogging.auth.service.AuthService;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.util.SecurityUtil;
import com.zone01oujda.moblogging.util.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        System.out.println(loginRequest.password);
        AuthTokensDto tokens = authService.login(loginRequest);
        AuthResponseDto response = new AuthResponseDto(tokens.getAccessToken());
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, "Login successful", response));
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AuthResponseDto>> register(
            @Valid @ModelAttribute RegisterRequestDto registerRequest) {
        System.out.println(registerRequest.password);

        AuthTokensDto tokens = authService.register(registerRequest);
        AuthResponseDto response = new AuthResponseDto(tokens.getAccessToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User registered successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, "Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentUserDto>> me() {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Unauthenticated"));
        }
        User user = authService.findUserByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "User not found"));
        }
        CurrentUserDto dto = new CurrentUserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                List.of(user.getRole().name()),
                user.getProfilePictureUrl());
        return ResponseEntity.ok(new ApiResponse<>(true, "Current user retrieved", dto));
    }
}
