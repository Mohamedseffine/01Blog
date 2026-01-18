package com.zone01oujda.moblogging.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zone01oujda.moblogging.entity.Comment;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    
}
