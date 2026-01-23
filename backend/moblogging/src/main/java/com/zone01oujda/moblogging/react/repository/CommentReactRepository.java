package com.zone01oujda.moblogging.react.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zone01oujda.moblogging.entity.CommentReact;
import com.zone01oujda.moblogging.react.enums.ReactType;

public interface CommentReactRepository extends JpaRepository<CommentReact, Long> {
    Optional<CommentReact> findByUserIdAndCommentId(Long userId, Long commentId);
    long countByCommentIdAndType(Long commentId, ReactType type);
    void deleteByUserIdAndCommentId(Long userId, Long commentId);
}
