package com.zone01oujda.moblogging.auth.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    public String usernameOrEmail;
    public String password;
}
