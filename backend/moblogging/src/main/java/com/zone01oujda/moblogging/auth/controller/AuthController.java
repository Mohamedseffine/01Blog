package com.zone01oujda.moblogging.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.auth.dto.AuthResponseDto;
import com.zone01oujda.moblogging.auth.dto.AuthTokensDto;
import com.zone01oujda.moblogging.auth.dto.LoginRequestDto;
import com.zone01oujda.moblogging.auth.dto.RegisterRequestDto;
import com.zone01oujda.moblogging.auth.service.AuthService;
import com.zone01oujda.moblogging.security.JwtTokenProvider;
import com.zone01oujda.moblogging.util.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String REFRESH_COOKIE_NAME = "refresh_token";

    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthService authService, JwtTokenProvider tokenProvider) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        System.out.println(loginRequest.password);
        AuthTokensDto tokens = authService.login(loginRequest);
        ResponseCookie refreshCookie = buildRefreshCookie(tokens.getRefreshToken());
        AuthResponseDto response = new AuthResponseDto(tokens.getAccessToken());
        return ResponseEntity.ok()
                .header("Set-Cookie", refreshCookie.toString())
                .body(new ApiResponse<>(true, "Login successful", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDto>> register(
            @Valid @RequestBody RegisterRequestDto registerRequest) {
        System.out.println(registerRequest.password);

        AuthTokensDto tokens = authService.register(registerRequest);
        ResponseCookie refreshCookie = buildRefreshCookie(tokens.getRefreshToken());
        AuthResponseDto response = new AuthResponseDto(tokens.getAccessToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Set-Cookie", refreshCookie.toString())
                .body(new ApiResponse<>(true, "User registered successfully", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDto>> refresh(
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Missing refresh token"));
        }
        AuthTokensDto tokens = authService.refreshTokens(refreshToken);
        ResponseCookie refreshCookie = buildRefreshCookie(tokens.getRefreshToken());
        AuthResponseDto dto = new AuthResponseDto(tokens.getAccessToken());
        return ResponseEntity.ok()
                .header("Set-Cookie", refreshCookie.toString())
                .body(new ApiResponse<>(true, "Token refreshed", dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = REFRESH_COOKIE_NAME, required = false) String refreshToken) {
        authService.revokeRefreshToken(refreshToken);
        ResponseCookie clearCookie = ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header("Set-Cookie", clearCookie.toString())
                .body(new ApiResponse<>(true, "Logged out successfully"));
    }

    private ResponseCookie buildRefreshCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(tokenProvider.getRefreshExpirationSeconds())
                .build();
    }
}
