package com.zone01oujda.moblogging.react.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zone01oujda.moblogging.entity.React;
import com.zone01oujda.moblogging.react.enums.ReactType;

public interface ReactRepository extends JpaRepository<React, Long> {
    Optional<React> findByUserIdAndPostId(Long userId, Long postId);
    long countByPostIdAndType(Long postId, ReactType type);
    void deleteByUserIdAndPostId(Long userId, Long postId);
}
