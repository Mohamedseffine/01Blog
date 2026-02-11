package com.zone01oujda.moblogging.post.dto;

import com.zone01oujda.moblogging.post.enums.PostVisibility;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePostDto {
    @Size(min = 3, max = 120, message = "Post title must be 3-120 characters")
    private String postTitle;

    @Size(min = 10, max = 5000, message = "Post content must be 10-5000 characters")
    private String postContent;
    private String[] postSubject;
    private PostVisibility postVisibility;
    private MultipartFile[] multipartFiles;
}
