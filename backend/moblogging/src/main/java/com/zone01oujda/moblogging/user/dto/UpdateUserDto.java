package com.zone01oujda.moblogging.user.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDto {
    @Size(min = 3, max = 15, message = "Username must be 3-15 characters")
    private String username;

    @Size(max = 280, message = "Bio must be 280 characters or fewer")
    private String bio;
    private MultipartFile profilePicture;
}
