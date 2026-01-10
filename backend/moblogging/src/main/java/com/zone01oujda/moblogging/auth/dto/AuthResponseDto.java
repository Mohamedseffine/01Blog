package com.zone01oujda.moblogging.auth.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    public String token;
    public AuthResponseDto(String token) {
        this.token = token;
    }
}
