package com.zone01oujda.moblogging.post.dto;

import org.springframework.web.multipart.MultipartFile;

import com.zone01oujda.moblogging.post.enums.PostVisibility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new post
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDto {

    /**
     * Post title (required, non-blank)
     */
    @NotBlank(message = "Post title is required")
    @Size(min = 3, max = 120, message = "Post title must be 3-120 characters")
    public String postTitle;

    /**
     * Post content (required, non-blank)
     */
    @NotBlank(message = "Post content is required")
    @Size(min = 10, max = 5000, message = "Post content must be 10-5000 characters")
    public String postContent;
    
    /**
     * Post subjects/tags (required, non-empty)
     */
    @NotEmpty(message = "Post must have at least one subject")
    public String[] postSubject;

    /**
     * Post visibility level (required)
     */
    @NotNull(message = "Post visibility is required")
    public PostVisibility postVisibility;

    /**
     * Media files (images/videos, optional)
     */
    public MultipartFile[] multipartFiles;
}
