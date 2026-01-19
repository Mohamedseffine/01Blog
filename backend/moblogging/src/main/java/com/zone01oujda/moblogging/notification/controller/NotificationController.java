package com.zone01oujda.moblogging.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zone01oujda.moblogging.util.response.ApiResponse;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Notifications retrieved successfully", null)
        );
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<Object>> getUnreadNotifications() {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Unread notifications retrieved successfully", null)
        );
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable("notificationId") Long notificationId) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Notification marked as read")
        );
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable("notificationId") Long notificationId) {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Notification deleted successfully")
        );
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        return ResponseEntity.ok(
            new ApiResponse<>(true, "All notifications marked as read")
        );
    }
    
}
