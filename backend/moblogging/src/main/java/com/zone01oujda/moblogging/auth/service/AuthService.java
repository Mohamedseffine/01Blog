package com.zone01oujda.moblogging.auth.service;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.auth.dto.AuthResponseDto;
import com.zone01oujda.moblogging.auth.dto.LoginRequestDto;
import com.zone01oujda.moblogging.auth.dto.RegisterRequestDto;
import com.zone01oujda.moblogging.entity.User;
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



        if (dto.password.length()>15) {
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

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.username, dto.password));

        String token = tokenProvider.generateToken(authentication);

        return new AuthResponseDto(token);
    }


    public AuthResponseDto login(LoginRequestDto dto) {
        if (dto.password.isEmpty()) {
            throw new RuntimeException("Password Is Empty");
        }

        if (dto.usernameOrEmail.isEmpty()){
            throw new RuntimeException("Username Or Email Is Empty");
        }

        Optional<User> userOpt = userRepository.findByUsernameOrEmail(dto.usernameOrEmail, dto.usernameOrEmail);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Username Or Email Not Found");
        }

        User user = userOpt.get();

        if ( user == null) {
            throw new RuntimeException("Username Or Email Not Found");
        }

        if (!passwordEncoder.matches(dto.password, user.getPassword())) {
            throw new RuntimeException("Wrong Password");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.usernameOrEmail, dto.password));

        String token = tokenProvider.generateToken(authentication);

        return new AuthResponseDto(token);
    }
}
