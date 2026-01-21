package com.zone01oujda.moblogging.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.auth.dto.AuthTokensDto;
import com.zone01oujda.moblogging.auth.dto.LoginRequestDto;
import com.zone01oujda.moblogging.auth.dto.RegisterRequestDto;
import com.zone01oujda.moblogging.auth.repository.RefreshTokenRepository;
import com.zone01oujda.moblogging.entity.RefreshToken;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.BadRequestException;
import com.zone01oujda.moblogging.exception.ConflictException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.security.JwtTokenProvider;
import com.zone01oujda.moblogging.user.enums.Role;
import com.zone01oujda.moblogging.user.repository.UserRepository;

/**
 * Service class for authentication operations
 */
@Service
public class AuthService {

    private static final int MAX_PASSWORD_LENGTH = 72;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user
     * 
     * @param dto the registration request DTO
     * @return AuthResponseDto with JWT token
     * @throws BadRequestException if registration data is invalid
     * @throws ConflictException   if user already exists
     */
    public AuthTokensDto register(RegisterRequestDto dto) {
        try {
            // Validation is handled by @Valid annotation in controller
            // Additional business logic validation
            if (!dto.password.equals(dto.confirmPassword)) {
                throw new BadRequestException("Passwords do not match");
            }

            if (dto.password.length() > MAX_PASSWORD_LENGTH) {
                throw new BadRequestException("Password exceeds maximum length");
            }

            if (userRepository.existsByEmail(dto.email)) {
                throw new ConflictException("Email already registered");
            }

            if (userRepository.existsByUsername(dto.username)) {
                throw new ConflictException("Username already taken");
            }

            // Create new user
            User user = new User(dto.username, dto.email, passwordEncoder.encode(dto.password));
            user.setDateOfBirth(dto.birthDate);
            user.setGender(dto.gender);
            user.setProfileType(dto.profileType);
            user.setFirstName(dto.firstName);
            user.setLastName(dto.lastName);
            user.setRole(Role.USER);

            User savedUser = userRepository.save(user);
            return issueTokens(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email or username already exists");
        }
    }

    /**
     * Login a user with credentials
     * 
     * @param dto the login request DTO
     * @return AuthResponseDto with JWT token
     * @throws BadRequestException       if credentials are invalid
     * @throws ResourceNotFoundException if user not found
     */
    public AuthTokensDto login(LoginRequestDto dto) {
        try {
            // Authenticate user with provided credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.usernameOrEmail, dto.password));

            // Retrieve user details
            User user = userRepository.findByUsernameOrEmail(authentication.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Generate JWT token
            return issueTokens(user);
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new BadRequestException("Invalid username/email or password");
        }
    }

    /**
     * Find user by username or email
     * 
     * @param username username or email
     * @return User or null
     */
    public User findUserByUsername(String username) {
        return userRepository.findByUsernameOrEmail(username).orElse(null);
    }

    public AuthTokensDto refreshTokens(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }

        String username = tokenProvider.getUsername(refreshToken);
        User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RefreshToken stored = refreshTokenRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new BadRequestException("Refresh token not found"));

        if (stored.getRevokedAt() != null) {
            throw new BadRequestException("Refresh token revoked");
        }

        if (stored.getExpiresAt() != null && stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Refresh token expired");
        }

        if (!sha256Base64(refreshToken).equals(stored.getTokenHash())) {
            throw new BadRequestException("Invalid refresh token");
        }

        return rotateTokens(user, stored);
    }

    public void revokeRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        if (!tokenProvider.validateToken(refreshToken)) {
            return;
        }
        String username = tokenProvider.getUsername(refreshToken);
        User user = userRepository.findByUsernameOrEmail(username).orElse(null);
        if (user == null) {
            return;
        }
        refreshTokenRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .ifPresent(token -> {
                    token.setRevokedAt(LocalDateTime.now());
                    refreshTokenRepository.save(token);
                });
    }

    private AuthTokensDto issueTokens(User user) {
        String access = tokenProvider.generateAccesToken(user);
        String refresh = tokenProvider.generateRefreshToken(user);
        upsertRefreshToken(user, refresh);
        return new AuthTokensDto(access, refresh);
    }

    private AuthTokensDto rotateTokens(User user, RefreshToken stored) {
        String access = tokenProvider.generateAccesToken(user);
        String refresh = tokenProvider.generateRefreshToken(user);
        stored.setTokenHash(sha256Base64(refresh));
        stored.setExpiresAt(LocalDateTime.now().plusSeconds(tokenProvider.getRefreshExpirationSeconds()));
        stored.setRevokedAt(null);
        refreshTokenRepository.save(stored);
        return new AuthTokensDto(access, refresh);
    }

    private void upsertRefreshToken(User user, String refreshToken) {
        RefreshToken token = refreshTokenRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseGet(() -> new RefreshToken(user));
        token.setTokenHash(sha256Base64(refreshToken));
        token.setExpiresAt(LocalDateTime.now().plusSeconds(tokenProvider.getRefreshExpirationSeconds()));
        token.setRevokedAt(null);
        refreshTokenRepository.save(token);
    }

    private String sha256Base64(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

}
