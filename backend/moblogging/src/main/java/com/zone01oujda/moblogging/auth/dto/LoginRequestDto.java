package com.zone01oujda.moblogging.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for login request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    
    /**
     * Username or email address
     */
    @NotBlank(message = "Username or email is required")
    public String usernameOrEmail;

    /**
     * Password (min 8, max 15 characters)
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters")
    public String password;
}
