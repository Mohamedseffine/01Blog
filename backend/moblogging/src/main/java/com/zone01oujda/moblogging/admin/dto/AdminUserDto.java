package com.zone01oujda.moblogging.admin.dto;

import java.time.LocalDateTime;

import com.zone01oujda.moblogging.user.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDto {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private boolean banned;
    private boolean blocked;
    private LocalDateTime createdAt;
}
