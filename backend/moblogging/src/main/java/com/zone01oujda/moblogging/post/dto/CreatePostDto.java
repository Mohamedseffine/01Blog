package com.zone01oujda.moblogging.post.dto;

import java.io.File;

import com.zone01oujda.moblogging.post.enums.PostVisibility;

import lombok.Data;

@Data
public class CreatePostDto {
    public String postTitle;
    public String postContent;
    public String postSubject;
    public PostVisibility postVisibility;
    public File[] multipartFiles;
}
