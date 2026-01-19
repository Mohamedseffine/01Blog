package com.zone01oujda.moblogging.post.dto;

import java.util.List;

import com.zone01oujda.moblogging.comment.dto.CommentDto;
import com.zone01oujda.moblogging.post.enums.PostVisibility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for post response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    
    /**
     * Post ID
     */
    public Long id;
    
    /**
     * Username of the post creator
     */
    public String creatorUsername;
    
    /**
     * Post title
     */
    public String postTitle;
    
    /**
     * Post content/body
     */
    public String postContent;
    
    /**
     * Post subjects/tags
     */
    public String[] postSubject;
    
    /**
     * Post visibility level
     */
    public PostVisibility postVisibility;
    
    /**
     * Media file URLs
     */
    public String[] medias;
    
    /**
     * List of comments on the post
     */
    public List<CommentDto> comments;

    /**
     * Constructor with basic post data
     */
    public PostDto(String postTitle, String postContent, String[] postSubject, 
                   PostVisibility postVisibility, String[] medias) {
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postSubject = postSubject;
        this.postVisibility = postVisibility;
        this.medias = medias;
    }
}
