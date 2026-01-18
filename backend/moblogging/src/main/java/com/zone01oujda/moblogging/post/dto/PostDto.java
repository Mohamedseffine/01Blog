package com.zone01oujda.moblogging.post.dto;

import java.util.List;

import com.zone01oujda.moblogging.comment.dto.CommentDto;
import com.zone01oujda.moblogging.post.enums.PostVisibility;

import lombok.Data;

@Data
public class PostDto {
    public String creatorUsername;
    public Long id;
    public String postTitle;
    public String postContent;
    public String[] postSubject;
    public PostVisibility postVisibility;
    public String[] medias;
    public List<CommentDto> comments;

    public PostDto(String postTitle, String postContent, String[] postSubject, PostVisibility postVisibility, String[] medias){
        this.medias = medias;
        this.postContent=postContent;
        this.postTitle = postTitle;
        this.postSubject= postSubject;
        this.postVisibility=postVisibility;
    }
}
