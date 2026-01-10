package com.zone01oujda.moblogging.auth.dto;

import java.time.LocalDate;

import com.zone01oujda.moblogging.user.enums.Gender;
import com.zone01oujda.moblogging.user.enums.ProfileType;

import lombok.Data;

@Data
public class RegisterRequestDto {
    public String firstName;
    public String lastName;
    public String username;
    public String email;
    public String password;
    public String confirmPassword;
    public LocalDate birthDate;
    public Gender gender;
    public ProfileType profileType;
}

