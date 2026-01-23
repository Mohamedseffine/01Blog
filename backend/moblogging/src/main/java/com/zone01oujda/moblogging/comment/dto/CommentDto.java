package com.zone01oujda.moblogging.comment.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    private Long parentId;
    private String content;
    private Boolean hidden;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Boolean modified;
    private List<CommentDto> children;
    private Long postId;
    private Long creatorId;
    private String creatorUsername;

    public CommentDto(Long id, Long parentId, String content, Boolean hidden, LocalDateTime createdAt,
                      LocalDateTime modifiedAt, Boolean modified, Long postId,
                      Long creatorId, String creatorUsername) {
        this.content = content;
        this.id = id;
        this.parentId = parentId;
        this.hidden = hidden;
        this.modified = modified;
        this.modifiedAt = modifiedAt;
        this.postId = postId;
        this.createdAt = createdAt;
        this.creatorId = creatorId;
        this.creatorUsername = creatorUsername;
    }
}
