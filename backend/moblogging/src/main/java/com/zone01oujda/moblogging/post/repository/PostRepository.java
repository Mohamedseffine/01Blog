package com.zone01oujda.moblogging.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zone01oujda.moblogging.entity.Post;
import com.zone01oujda.moblogging.post.enums.PostVisibility;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("""
        SELECT p FROM Post p
        WHERE (:visibility IS NULL OR p.visibility = :visibility)
          AND (:hidden IS NULL OR p.hidden = :hidden)
          AND (:creatorId IS NULL OR p.creator.id = :creatorId)
          AND (:creatorUsername IS NULL OR lower(p.creator.username) LIKE lower(concat('%', :creatorUsername, '%')))
        """)
    Page<Post> findForAdmin(
            @Param("visibility") PostVisibility visibility,
            @Param("hidden") Boolean hidden,
            @Param("creatorId") Long creatorId,
            @Param("creatorUsername") String creatorUsername,
            Pageable pageable);
}
