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
import com.zone01oujda.moblogging.exception.ConflictException;
import com.zone01oujda.moblogging.security.JwtTokenProvider;
import com.zone01oujda.moblogging.user.enums.Role;
import com.zone01oujda.moblogging.user.repository.UserRepository;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider){

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;

    }

    public AuthResponseDto register(RegisterRequestDto dto) {
        try {
            if (!dto.password.equals(dto.confirmPassword) ) {
                throw new RuntimeException("Password Do Not Match");
            }

            if ( dto.password.isEmpty()) {
                throw new RuntimeException("Password Is Empty");
            }
            
            if ( dto.email.isEmpty()) {
                throw new RuntimeException("Email Is Empty");
            }
            
            if ( dto.username.isEmpty()) {
                throw new RuntimeException("Username Is Empty");
            }

            if ( dto.firstName.isEmpty()) {
                throw new RuntimeException("Firstname Is Empty");
            }

            if ( dto.lastName.isEmpty()) {
                throw new RuntimeException("Lastname Is Empty");
            }



            if (dto.password.length()>32) {
                throw new RuntimeException("Password Exceeds Maximum Lenght");
            }



            if (userRepository.existsByEmail(dto.email)) {
                throw new RuntimeException("Email Already Exists");
            }

            if (userRepository.existsByUsername(dto.username)) {
                throw new RuntimeException("Username ALready Exists");
            }

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
        }catch (DataIntegrityViolationException e)  {
            throw new ConflictException("email or username already exists");
        }
    }


    public AuthResponseDto login(LoginRequestDto dto) {
        if (dto.password.isEmpty()) {
            throw new RuntimeException("Password Is Empty");
        }

        if (dto.usernameOrEmail.isEmpty()){
            throw new RuntimeException("Username Or Email Is Empty");
        }
                                
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.usernameOrEmail, dto.password));
        
        
        User user = userRepository.findByUsernameOrEmail(authentication.getName()).orElseThrow();
        
        String token = tokenProvider.generateAccesToken(user);

        return new AuthResponseDto(token);
    }
}
