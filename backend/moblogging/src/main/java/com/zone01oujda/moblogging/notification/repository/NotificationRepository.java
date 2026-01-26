package com.zone01oujda.moblogging.notification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.zone01oujda.moblogging.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);
    List<Notification> findByReceiverIdAndStateFalseOrderByCreatedAtDesc(Long receiverId);
    long countByReceiverIdAndStateFalse(Long receiverId);
    Optional<Notification> findByIdAndReceiverId(Long id, Long receiverId);
}
