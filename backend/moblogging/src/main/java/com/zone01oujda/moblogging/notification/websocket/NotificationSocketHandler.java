package com.zone01oujda.moblogging.notification.websocket;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.zone01oujda.moblogging.notification.dto.NotificationDto;

/**
 * WebSocket controller for handling real-time notifications
 * Manages STOMP message routing for notification delivery
 */
@Controller
public class NotificationSocketHandler {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    // Track connected users: userId -> session count
    private final ConcurrentHashMap<Long, Integer> connectedUsers = new ConcurrentHashMap<>();

    public NotificationSocketHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Send notification to a specific user
     * Called when a new notification is created
     * @param userId the recipient user ID
     * @param notification the notification to send
     */
    public void sendNotificationToUser(Long userId, NotificationDto notification) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notifications",
            notification
        );
    }

    /**
     * Send notification to all connected users
     * Used for broadcast notifications (e.g., system announcements)
     * @param notification the notification to broadcast
     */
    public void broadcastNotification(NotificationDto notification) {
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    /**
     * Handle incoming notification messages from client
     * @param notification the notification payload
     */
    @MessageMapping("/notifications/send")
    @SendToUser("/queue/notifications")
    public NotificationDto handleNotification(NotificationDto notification) {
        return notification;
    }

    /**
     * Handle user connection
     * Tracks when a user connects
     * @param userId the connected user ID
     */
    @MessageMapping("/users/connect")
    public void handleUserConnect(Long userId) {
        try {
            connectedUsers.compute(userId, (k, v) -> v == null ? 1 : v + 1);
        } catch (NumberFormatException e) {
            // Log invalid user ID
        }
    }

    /**
     * Handle user disconnection
     * Tracks when a user disconnects
     * @param userId the disconnected user ID
     */
    @MessageMapping("/users/disconnect")
    public void handleUserDisconnect(Long userId) {
        try {
            
            connectedUsers.computeIfPresent(userId, (k, v) -> v > 1 ? v - 1 : null);
        } catch (NumberFormatException e) {
            // Log invalid user ID
        }
    }

    /**
     * Check if a user is currently connected
     * @param userId the user ID to check
     * @return true if user has active connections
     */
    public boolean isUserConnected(Long userId) {
        return connectedUsers.containsKey(userId);
    }

    /**
     * Get number of connected sessions for a user
     * @param userId the user ID
     * @return number of active sessions, 0 if not connected
     */
    public int getConnectedSessionCount(Long userId) {
        return connectedUsers.getOrDefault(userId, 0);
    }

    /**
     * Get total number of connected users
     * @return count of users with active connections
     */
    public int getTotalConnectedUsers() {
        return connectedUsers.size();
    }
}
