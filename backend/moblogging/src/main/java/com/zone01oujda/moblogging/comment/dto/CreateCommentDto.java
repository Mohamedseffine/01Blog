package com.zone01oujda.moblogging.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCommentDto {

    @NotEmpty(message = "the content is empty")
    private String content;

    @NotNull(message = "post id is null")
    private Long postId;
    
    private Long parentId;

}
