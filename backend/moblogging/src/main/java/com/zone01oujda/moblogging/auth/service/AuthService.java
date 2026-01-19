package com.zone01oujda.moblogging.auth.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.auth.dto.AuthResponseDto;
import com.zone01oujda.moblogging.auth.dto.LoginRequestDto;
import com.zone01oujda.moblogging.auth.dto.RegisterRequestDto;
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
    
    private static final int MAX_PASSWORD_LENGTH = 32;
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user
     * @param dto the registration request DTO
     * @return AuthResponseDto with JWT token
     * @throws BadRequestException if registration data is invalid
     * @throws ConflictException if user already exists
     */
    public AuthResponseDto register(RegisterRequestDto dto) {
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
            String token = tokenProvider.generateAccesToken(savedUser);

            return new AuthResponseDto(token);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email or username already exists");
        }
    }

    /**
     * Login a user with credentials
     * @param dto the login request DTO
     * @return AuthResponseDto with JWT token
     * @throws BadRequestException if credentials are invalid
     * @throws ResourceNotFoundException if user not found
     */
    public AuthResponseDto login(LoginRequestDto dto) {
        try {
            // Authenticate user with provided credentials
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.usernameOrEmail, dto.password)
            );

            // Retrieve user details
            User user = userRepository.findByUsernameOrEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Generate JWT token
            String token = tokenProvider.generateAccesToken(user);

            return new AuthResponseDto(token);
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new BadRequestException("Invalid username/email or password");
        }
    }
}
