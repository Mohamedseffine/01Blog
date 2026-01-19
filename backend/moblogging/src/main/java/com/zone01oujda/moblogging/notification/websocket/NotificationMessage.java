package com.zone01oujda.moblogging.notification.websocket;

import com.zone01oujda.moblogging.notification.dto.NotificationDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket message for real-time notifications
 * Wraps notification data with metadata
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    
    /**
     * Message type (NOTIFICATION, STATUS, ERROR, etc.)
     */
    private String messageType;
    
    /**
     * The notification payload
     */
    private NotificationDto notification;
    
    /**
     * Recipient user ID
     */
    private Long recipientId;
    
    /**
     * Timestamp when message was created
     */
    private Long timestamp;
    
    /**
     * Constructor with notification and type
     */
    public NotificationMessage(String messageType, NotificationDto notification, Long recipientId) {
        this.messageType = messageType;
        this.notification = notification;
        this.recipientId = recipientId;
        this.timestamp = System.currentTimeMillis();
    }
}
