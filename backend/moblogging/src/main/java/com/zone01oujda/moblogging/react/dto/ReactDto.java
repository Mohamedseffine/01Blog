package com.zone01oujda.moblogging.react.dto;

import com.zone01oujda.moblogging.react.enums.ReactType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for reactions (likes, loves, etc.)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactDto {
    
    /**
     * Type of reaction
     */
    @NotNull(message = "Reaction type is required")
    public ReactType reactType;
    
    /**
     * ID of the reacting user
     */
    public Long userId;
    
    /**
     * ID of the post being reacted to
     */
    public Long postId;
}
