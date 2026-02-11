package com.zone01oujda.moblogging.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(min = 1, max = 500, message = "Comment must be 1-500 characters")
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
