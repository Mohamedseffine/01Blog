package com.zone01oujda.moblogging.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/auth")
public class AuthController {
    @GetMapping("/login")
    public String loginString() {
        System.out.println("ana hna");
        return "ana jit";
    }

    @GetMapping("/register")
    public String postMethodName() {
        System.out.println("la ana hna");
        return "hya li bghat";
    }
    
    
}
