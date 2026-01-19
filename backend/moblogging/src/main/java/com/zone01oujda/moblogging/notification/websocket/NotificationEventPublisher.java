package com.zone01oujda.moblogging.notification.websocket;

import org.springframework.stereotype.Service;

import com.zone01oujda.moblogging.notification.dto.NotificationDto;

/**
 * Service for publishing notification events
 * Handles sending notifications to users via WebSocket
 */
@Service
public class NotificationEventPublisher {
    
    private final NotificationSocketHandler socketHandler;

    public NotificationEventPublisher(NotificationSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    /**
     * Publish notification to a specific user
     * @param userId the recipient user ID
     * @param notification the notification to send
     */
    public void publishToUser(Long userId, NotificationDto notification) {
        if (socketHandler.isUserConnected(userId)) {
            socketHandler.sendNotificationToUser(userId, notification);
        }
    }

    /**
     * Publish notification to all users
     * @param notification the notification to broadcast
     */
    public void publishToAll(NotificationDto notification) {
        socketHandler.broadcastNotification(notification);
    }

    /**
     * Publish notification to multiple users
     * @param userIds array of recipient user IDs
     * @param notification the notification to send
     */
    public void publishToUsers(Long[] userIds, NotificationDto notification) {
        for (Long userId : userIds) {
            publishToUser(userId, notification);
        }
    }

    /**
     * Check if user is connected
     * @param userId the user ID
     * @return true if user has active WebSocket connection
     */
    public boolean isUserOnline(Long userId) {
        return socketHandler.isUserConnected(userId);
    }

    /**
     * Get connection status for a user
     * @param userId the user ID
     * @return number of active connections for the user
     */
    public int getUserConnectionCount(Long userId) {
        return socketHandler.getConnectedSessionCount(userId);
    }

    /**
     * Get total connected users count
     * @return number of currently connected users
     */
    public int getTotalOnlineUsers() {
        return socketHandler.getTotalConnectedUsers();
    }
}
