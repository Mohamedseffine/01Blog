package com.zone01oujda.moblogging.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.notification.websocket.NotificationEventPublisher;
import com.zone01oujda.moblogging.util.response.ApiResponse;

/**
 * REST controller for notification operations
 * Handles HTTP requests for notification management
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationEventPublisher eventPublisher;

    public NotificationController(NotificationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Get paginated notifications for current user
     * @param page page number (default 0)
     * @param size page size (default 10)
     * @return list of notifications
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Notifications retrieved successfully", null)
        );
    }

    /**
     * Get unread notifications for current user
     * @return list of unread notifications
     */
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<Object>> getUnreadNotifications() {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Unread notifications retrieved successfully", null)
        );
    }

    /**
     * Mark a notification as read
     * @param notificationId the notification ID
     * @return success response
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable("notificationId") Long notificationId) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Notification marked as read")
        );
    }

    /**
     * Delete a notification
     * @param notificationId the notification ID to delete
     * @return success response
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable("notificationId") Long notificationId) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Notification deleted successfully")
        );
    }

    /**
     * Mark all notifications as read for current user
     * @return success response
     */
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "All notifications marked as read")
        );
    }

    /**
     * Get user online status
     * @param userId the user ID to check
     * @return online status
     */
    @GetMapping("/status/{userId}")
    public ResponseEntity<ApiResponse<Object>> getUserStatus(@PathVariable("userId") Long userId) {
        java.util.Map<String, Object> status = new java.util.HashMap<>();
        status.put("userId", userId);
        status.put("isOnline", eventPublisher.isUserOnline(userId));
        status.put("connectionCount", eventPublisher.getUserConnectionCount(userId));
        
        return ResponseEntity.ok(
            new ApiResponse<>(true, "User status retrieved", status)
        );
    }

    /**
     * Get system statistics about connected users
     * @return connection statistics
     */
    @GetMapping("/system/stats")
    public ResponseEntity<ApiResponse<Object>> getSystemStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalOnlineUsers", eventPublisher.getTotalOnlineUsers());
        stats.put("timestamp", java.time.LocalDateTime.now());
        
        return ResponseEntity.ok(
            new ApiResponse<>(true, "System statistics retrieved", stats)
        );
    }
}
