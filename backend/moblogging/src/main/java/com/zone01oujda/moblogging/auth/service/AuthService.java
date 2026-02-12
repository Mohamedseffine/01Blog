package com.zone01oujda.moblogging.auth.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.auth.dto.AuthTokensDto;
import com.zone01oujda.moblogging.auth.dto.LoginRequestDto;
import com.zone01oujda.moblogging.auth.dto.RegisterRequestDto;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.BadRequestException;
import com.zone01oujda.moblogging.exception.ConflictException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.security.JwtTokenProvider;
import com.zone01oujda.moblogging.user.enums.Role;
import com.zone01oujda.moblogging.user.repository.UserRepository;
import com.zone01oujda.moblogging.util.FileUploadUtil;

/**
 * Service class for authentication operations
 */
@Service
public class AuthService {

    private static final int MAX_PASSWORD_LENGTH = 72;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final FileUploadUtil fileUploadUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
            FileUploadUtil fileUploadUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.fileUploadUtil = fileUploadUtil;
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
            String email = trimToNull(dto.email);
            String username = trimToNull(dto.username);
            String firstName = trimToNull(dto.firstName);
            String lastName = trimToNull(dto.lastName);

            // Validation is handled by @Valid annotation in controller
            // Additional business logic validation
            if (!dto.password.equals(dto.confirmPassword)) {
                throw new BadRequestException("Passwords do not match");
            }

            if (dto.password.length() > MAX_PASSWORD_LENGTH) {
                throw new BadRequestException("Password exceeds maximum length");
            }

            if (email == null) {
                throw new BadRequestException("Email is required");
            }
            if (userRepository.existsByEmail(email)) {
                throw new ConflictException("Email already registered");
            }

            if (username == null) {
                throw new BadRequestException("Username is required");
            }
            if (userRepository.existsByUsername(username)) {
                throw new ConflictException("Username already taken");
            }

            // Create new user
            User user = new User(username, email, passwordEncoder.encode(dto.password));
            user.setDateOfBirth(dto.birthDate);
            user.setGender(dto.gender);
            user.setProfileType(dto.profileType);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRole(Role.USER);
            if (dto.profilePicture != null && !dto.profilePicture.isEmpty()) {
                String uploadedPath = fileUploadUtil.upload(dto.profilePicture);
                user.setProfilePictureUrl(uploadedPath);
            }

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
            String usernameOrEmail = trimToNull(dto.usernameOrEmail);
            if (usernameOrEmail == null) {
                throw new BadRequestException("Invalid username/email or password");
            }
            // Authenticate user with provided credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameOrEmail, dto.password));

            // Retrieve user details
            User user = userRepository.findByUsernameOrEmail(authentication.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (user.isBanned()) {
                throw new BadRequestException("Your account is banned. Please contact support.");
            }

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

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private AuthTokensDto issueTokens(User user) {
        String access = tokenProvider.generateAccesToken(user);
        return new AuthTokensDto(access);
    }

}
