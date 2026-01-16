package com.zone01oujda.moblogging.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zone01oujda.moblogging.entity.Post;

public interface  PostRepository extends JpaRepository<Post, Long> {
    
}
