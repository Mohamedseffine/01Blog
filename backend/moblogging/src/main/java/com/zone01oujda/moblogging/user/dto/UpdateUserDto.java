package com.zone01oujda.moblogging.user.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UpdateUserDto {
    private String username;
    private String bio;
    private MultipartFile profilePicture;
}
