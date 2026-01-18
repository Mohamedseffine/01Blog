package com.zone01oujda.moblogging.post.util;



import com.zone01oujda.moblogging.comment.util.CommentMapper;
import com.zone01oujda.moblogging.entity.Post;
import com.zone01oujda.moblogging.post.dto.PostDto;

public class PostMapper {
    private PostMapper() {
    }

    public static PostDto toDto(Post post) {
        if (post == null)
            return null;

        PostDto dto = new PostDto(post.getTitle(), post.getContent(), post.getSubject().split(","), post.getVisibility(), post.getMediaUrl().split(",")) ;

        dto.setCreatorUsername(post.getCreator().getUsername());
        dto.setId(post.getId());

        if (post.getComments() != null && !post.getComments().isEmpty()) {
            dto.setComments(
                    post.getComments()
                            .stream()
                            .map(CommentMapper::toDto)
                            .toList());
        }

        return dto;
    }
}
