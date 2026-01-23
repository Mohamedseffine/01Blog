package com.zone01oujda.moblogging.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zone01oujda.moblogging.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostIdAndHiddenFalse(Long postId, Pageable pageable);

    @Query("""
        SELECT c FROM Comment c
        WHERE (:hidden IS NULL OR c.hidden = :hidden)
          AND (:postId IS NULL OR c.post.id = :postId)
          AND (:creatorId IS NULL OR c.creator.id = :creatorId)
        """)
    Page<Comment> findForAdmin(
            @Param("hidden") Boolean hidden,
            @Param("postId") Long postId,
            @Param("creatorId") Long creatorId,
            Pageable pageable);
}
