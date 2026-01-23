package com.zone01oujda.moblogging.post.dto;

import com.zone01oujda.moblogging.post.enums.PostVisibility;

import lombok.Data;

@Data
public class UpdatePostDto {
    private String postTitle;
    private String postContent;
    private String[] postSubject;
    private PostVisibility postVisibility;
}
