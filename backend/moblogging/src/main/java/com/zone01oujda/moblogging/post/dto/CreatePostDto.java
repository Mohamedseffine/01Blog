package com.zone01oujda.moblogging.post.dto;


import org.springframework.web.multipart.MultipartFile;

import com.zone01oujda.moblogging.post.enums.PostVisibility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePostDto {

    @NotBlank(message="post title is empty")
    public String postTitle;

    @NotBlank(message= "post content is empty")
    public String postContent;
    
    @NotNull(message="post subject is empty")
    public String[] postSubject;

    @NotNull(message="post visibility is empty")
    public PostVisibility postVisibility;


    public MultipartFile[] multipartFiles;
}
