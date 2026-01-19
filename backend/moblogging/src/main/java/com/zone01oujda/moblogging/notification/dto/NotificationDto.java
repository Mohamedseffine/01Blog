package com.zone01oujda.moblogging.notification.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for notifications
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    
    /**
     * Notification ID
     */
    public Long id;
    
    /**
     * Notification message
     */
    public String message;
    
    /**
     * Whether notification has been read
     */
    public boolean isRead;
    
    /**
     * Type of notification
     */
    public String type;
    
    /**
     * Related content ID (post, comment, etc.)
     */
    public Long contentId;
    
    /**
     * Timestamp when notification was created
     */
    public LocalDateTime createdAt;
}
