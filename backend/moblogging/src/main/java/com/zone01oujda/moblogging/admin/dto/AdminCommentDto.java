package com.zone01oujda.moblogging.admin.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminCommentDto {
    private Long id;
    private String content;
    private Long postId;
    private Long creatorId;
    private String creatorUsername;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Boolean hidden;
}
