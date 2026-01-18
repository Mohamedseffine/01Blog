package com.zone01oujda.moblogging.comment.util;

import com.zone01oujda.moblogging.comment.dto.CommentDto;
import com.zone01oujda.moblogging.entity.Comment;

public final class CommentMapper {

    private CommentMapper() {
    }

    public static CommentDto toDto(Comment comment) {
        if (comment == null)
            return null;

        CommentDto dto = new CommentDto(comment.getId(), comment.getParent() != null ? comment.getParent().getId() : 0, comment.getContent(),
                comment.getHidden(), comment.getCreatedAt(), comment.getModifiedAt(), comment.getModified(),
                comment.getPost().getId());

        dto.setParentId(
                comment.getParent() == null ? 0 : comment.getParent().getId());

        if (comment.getChildren() != null && !comment.getChildren().isEmpty()) {
            dto.setChildren(
                    comment.getChildren()
                            .stream()
                            .map(CommentMapper::toDto)
                            .toList());
        }

        return dto;
    }
}
