package com.zone01oujda.moblogging.notification.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.entity.Notification;
import com.zone01oujda.moblogging.entity.User;
import com.zone01oujda.moblogging.exception.AccessDeniedException;
import com.zone01oujda.moblogging.exception.ResourceNotFoundException;
import com.zone01oujda.moblogging.notification.dto.NotificationDto;
import com.zone01oujda.moblogging.notification.enums.NotificationType;
import com.zone01oujda.moblogging.notification.repository.NotificationRepository;
import com.zone01oujda.moblogging.notification.websocket.NotificationEventPublisher;
import com.zone01oujda.moblogging.user.repository.UserRepository;
import com.zone01oujda.moblogging.util.SecurityUtil;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationEventPublisher eventPublisher;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository,
            NotificationEventPublisher eventPublisher) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    public Page<NotificationDto> getNotifications(int page, int size) {
        User user = getCurrentUser();
        return notificationRepository
            .findByReceiverIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size))
            .map(this::toDto);
    }

    public List<NotificationDto> getUnreadNotifications() {
        User user = getCurrentUser();
        return notificationRepository
            .findByReceiverIdAndStateFalseOrderByCreatedAtDesc(user.getId())
            .stream()
            .map(this::toDto)
            .toList();
    }

    public long getUnreadCount() {
        User user = getCurrentUser();
        return notificationRepository.countByReceiverIdAndStateFalse(user.getId());
    }

    public void markAsRead(Long notificationId) {
        User user = getCurrentUser();
        Notification notification = notificationRepository.findByIdAndReceiverId(notificationId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setState(true);
        notificationRepository.save(notification);
    }

    public void markAsUnread(Long notificationId) {
        User user = getCurrentUser();
        Notification notification = notificationRepository.findByIdAndReceiverId(notificationId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setState(false);
        notificationRepository.save(notification);
    }

    public void markAllAsRead() {
        User user = getCurrentUser();
        List<Notification> unread = notificationRepository
            .findByReceiverIdAndStateFalseOrderByCreatedAtDesc(user.getId());
        if (unread.isEmpty()) {
            return;
        }
        unread.forEach(notification -> notification.setState(true));
        notificationRepository.saveAll(unread);
    }

    public void deleteNotification(Long notificationId) {
        User user = getCurrentUser();
        Notification notification = notificationRepository.findByIdAndReceiverId(notificationId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notificationRepository.delete(notification);
    }

    public NotificationDto createNotification(User receiver, NotificationType type, String message) {
        Notification notification = new Notification(type, message, receiver);
        Notification saved = notificationRepository.save(notification);
        NotificationDto dto = toDto(saved);
        eventPublisher.publishToUser(receiver, dto);
        return dto;
    }

    private User getCurrentUser() {
        String username = SecurityUtil.getCurrentUsername();
        if (username == null) {
            throw new AccessDeniedException("User not authenticated");
        }
        return userRepository.findByUsernameOrEmail(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private NotificationDto toDto(Notification notification) {
        return new NotificationDto(
            notification.getId(),
            notification.getContent(),
            Boolean.TRUE.equals(notification.getState()),
            notification.getType() != null ? notification.getType().name() : "SYSTEM",
            null,
            notification.getCreatedAt()
        );
    }
}
