package com.zone01oujda.moblogging.user.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String profilePicture;
    private int followersCount;
    private int followingCount;
    private LocalDateTime createdAt;
    private boolean isFollowing;
}
