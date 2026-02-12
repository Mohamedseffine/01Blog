package com.zone01oujda.moblogging.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing comment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentDto {

    /**
     * Comment content (required, non-empty)
     */
    @NotEmpty(message = "Comment content is required")
    @Size(min = 1, max = 500, message = "Comment must be 1-500 characters")
    public String content;
}
