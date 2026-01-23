package com.zone01oujda.moblogging.auth.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrentUserDto {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private String profilePicture;
}
