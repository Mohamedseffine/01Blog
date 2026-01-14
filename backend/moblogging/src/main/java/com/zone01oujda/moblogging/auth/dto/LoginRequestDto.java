package com.zone01oujda.moblogging.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank(message = "Tere is no username or email")
    public String usernameOrEmail;


    @NotBlank(message = "Password is empty")
    @Size(min = 8, max = 15)
    public String password;
}
