package com.zone01oujda.moblogging.auth.dto;

import java.time.LocalDate;

import com.zone01oujda.moblogging.user.enums.Gender;
import com.zone01oujda.moblogging.user.enums.ProfileType;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    
    /**
     * First name (required, 3-15 characters)
     */
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 30, message = "First name must be 2-30 characters")
    public String firstName;

    /**
     * Last name (required, 3-15 characters)
     */
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 30, message = "Last name must be 2-30 characters")
    public String lastName;

    /**
     * Username (required, 3-15 characters, must be unique)
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 15, message = "Username must be 3-15 characters")
    public String username;

    /**
     * Email address (required, must be valid and unique)
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 120, message = "Email must be 120 characters or fewer")
    public String email;

    /**
     * Password (required, 10-72 characters)
     */
    @NotBlank(message = "Password is required")
    @Size(min = 10, max = 72, message = "Password must be 10-72 characters")
    public String password;

    /**
     * Password confirmation (must match password)
     */
    @NotBlank(message = "Confirm password is required")
    @Size(min = 10, max = 72, message = "Password must be 10-72 characters")
    public String confirmPassword;

    /**
     * Date of birth (must be in the past)
     */
    @Past(message = "Birth date must be in the past")
    public LocalDate birthDate;

    /**
     * Gender (required)
     */
    @NotNull(message = "Gender is required")
    public Gender gender;

    /**
     * Profile type (required)
     */
    @NotNull(message = "Profile type is required")
    public ProfileType profileType;

    /**
     * Optional profile picture
     */
    public MultipartFile profilePicture;
}
