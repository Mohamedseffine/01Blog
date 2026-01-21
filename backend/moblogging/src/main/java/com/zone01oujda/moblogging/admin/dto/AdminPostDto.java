package com.zone01oujda.moblogging.admin.dto;

import java.time.LocalDateTime;

import com.zone01oujda.moblogging.post.enums.PostVisibility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminPostDto {
    private Long id;
    private String title;
    private PostVisibility visibility;
    private LocalDateTime createdAt;
    private Boolean hidden;
    private Long creatorId;
    private String creatorUsername;
}
