package com.zone01oujda.moblogging.auth.dto;

import java.time.LocalDateTime;

public class RegisterRequestDto {
    public String username;
    public String email;
    public String password;
    public String confirmPassword;
    public LocalDateTime birthDate;
    public String gender;
    public String profileType;
}
