package com.zone01oujda.moblogging.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new comment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDto {

    /**
     * Comment content (required, non-empty)
     */
    @NotEmpty(message = "Comment content is required")
    public String content;

    /**
     * Post ID that the comment belongs to (required)
     */
    @NotNull(message = "Post ID is required")
    public Long postId;
    
    /**
     * Parent comment ID for nested replies (optional)
     */
    public Long parentId;
}
