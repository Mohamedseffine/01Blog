package com.zone01oujda.moblogging.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for follow/unfollow requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowRequestDto {
    
    /**
     * ID of the user to follow/unfollow
     */
    @NotNull(message = "User ID is required")
    public Long userId;
}
