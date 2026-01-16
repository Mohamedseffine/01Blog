package com.zone01oujda.moblogging.post.dto;

import com.zone01oujda.moblogging.post.enums.PostVisibility;

import lombok.Data;

@Data
public class PostDto {
    public String postTitle;
    public String postContent;
    public String postSubject;
    public PostVisibility postVisibility;
    public String[] medias;

    public PostDto(String postTitle, String postContent, String postSubject, PostVisibility postVisibility, String[] medias){
        this.medias = medias;
        this.postContent=postContent;
        this.postTitle = postTitle;
        this.postSubject= postSubject;
        this.postVisibility=postVisibility;
    }
}
