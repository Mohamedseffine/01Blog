package com.zone01oujda.moblogging.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.auth.dto.AuthResponseDto;
import com.zone01oujda.moblogging.auth.dto.LoginRequestDto;
import com.zone01oujda.moblogging.auth.dto.RegisterRequestDto;
import com.zone01oujda.moblogging.auth.service.AuthService;




@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginRequestDto login(@RequestBody LoginRequestDto entity) {
        System.out.println("ana hna");
        return entity;
    }

    @PostMapping("/register")
    public AuthResponseDto register(@RequestBody RegisterRequestDto entity) {
        return authService.register(entity);
    }
    
    
}
