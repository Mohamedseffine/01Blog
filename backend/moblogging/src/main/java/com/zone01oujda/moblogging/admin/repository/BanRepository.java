package com.zone01oujda.moblogging.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zone01oujda.moblogging.entity.Ban;

public interface BanRepository extends JpaRepository<Ban, Long> {
    Optional<Ban> findTopByUserIdOrderByCreatedAtDesc(Long userId);
    void deleteByUserId(Long userId);
}
