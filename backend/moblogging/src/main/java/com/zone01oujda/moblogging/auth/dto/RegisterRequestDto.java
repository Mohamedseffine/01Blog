package com.zone01oujda.moblogging.auth.dto;

import java.time.LocalDate;

import com.zone01oujda.moblogging.user.enums.Gender;
import com.zone01oujda.moblogging.user.enums.ProfileType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDto {
    @NotBlank(message = "The firstname is empty")
    @Size(min = 3, max = 15)
    public String firstName;

    @NotBlank(message = "The lastname is empty")
    @Size(min = 3, max = 15)
    public String lastName;

    @NotBlank(message = "The username is empty")
    @Size(min = 3, max = 15)
    public String username;

    @NotBlank(message = "The email is empty")
    @Email(message="email not valid")
    public String email;

    @NotBlank(message = "The password is empty")
    @Size(min = 10, max = 32)
    public String password;

    @NotBlank(message = "The password is empty")
    @Size(min = 10, max = 32)
    public String confirmPassword;

    @Past(message="the birthdate must be in past")
    public LocalDate birthDate;

    @NotNull(message = "The gender is empty")
    public Gender gender;

    @NotNull(message = "there is no profile type")
    public ProfileType profileType;
}

