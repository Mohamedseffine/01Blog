/**
 * WebSocket Notification Client
 * Handles real-time notification WebSocket connections
 */
class NotificationWebSocketClient {
    constructor(userId, baseUrl = window.location.origin) {
        this.userId = userId;
        this.baseUrl = baseUrl;
        this.stompClient = null;
        this.isConnected = false;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectDelay = 1000; // 1 second
        this.callbacks = {};
    }

    /**
     * Connect to WebSocket server
     */
    connect() {
        const socketUrl = `${this.baseUrl}/ws`;
        const socket = new SockJS(socketUrl);
        
        this.stompClient = Stomp.over(socket);
        
        this.stompClient.connect({}, 
            (frame) => this.onConnected(frame),
            (error) => this.onConnectError(error)
        );
    }

    /**
     * Handle successful connection
     */
    onConnected(frame) {
        console.log('WebSocket connected', frame);
        this.isConnected = true;
        this.reconnectAttempts = 0;

        // Notify server of connection
        this.stompClient.send('/app/users/connect', {}, this.userId);

        // Subscribe to user's personal notification queue
        this.stompClient.subscribe(`/user/${this.userId}/queue/notifications`, 
            (message) => this.onNotificationReceived(message)
        );

        // Subscribe to broadcast notifications
        this.stompClient.subscribe('/topic/notifications',
            (message) => this.onBroadcastNotification(message)
        );

        // Trigger callback
        if (this.callbacks.onConnected) {
            this.callbacks.onConnected();
        }
    }

    /**
     * Handle connection error
     */
    onConnectError(error) {
        console.error('WebSocket connection error:', error);
        this.isConnected = false;
        this.attemptReconnect();
    }

    /**
     * Handle incoming notification
     */
    onNotificationReceived(message) {
        try {
            const notification = JSON.parse(message.body);
            console.log('Notification received:', notification);
            
            if (this.callbacks.onNotification) {
                this.callbacks.onNotification(notification);
            }
        } catch (error) {
            console.error('Error parsing notification:', error);
        }
    }

    /**
     * Handle broadcast notification
     */
    onBroadcastNotification(message) {
        try {
            const notification = JSON.parse(message.body);
            console.log('Broadcast notification received:', notification);
            
            if (this.callbacks.onBroadcast) {
                this.callbacks.onBroadcast(notification);
            }
        } catch (error) {
            console.error('Error parsing broadcast:', error);
        }
    }

    /**
     * Send notification message
     */
    sendNotification(notification) {
        if (this.isConnected) {
            this.stompClient.send('/app/notifications/send', {}, 
                JSON.stringify(notification)
            );
        } else {
            console.warn('WebSocket not connected');
        }
    }

    /**
     * Disconnect from WebSocket
     */
    disconnect() {
        if (this.stompClient && this.isConnected) {
            this.stompClient.send('/app/users/disconnect', {}, this.userId);
            this.stompClient.disconnect(() => {
                console.log('WebSocket disconnected');
                this.isConnected = false;
            });
        }
    }

    /**
     * Attempt to reconnect
     */
    attemptReconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            const delay = this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1);
            console.log(`Attempting to reconnect in ${delay}ms...`);
            
            setTimeout(() => this.connect(), delay);
        } else {
            console.error('Max reconnection attempts reached');
            if (this.callbacks.onMaxReconnectAttemptsReached) {
                this.callbacks.onMaxReconnectAttemptsReached();
            }
        }
    }

    /**
     * Register callback
     */
    on(event, callback) {
        this.callbacks[event] = callback;
    }

    /**
     * Check if connected
     */
    isWebSocketConnected() {
        return this.isConnected && this.stompClient;
    }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = NotificationWebSocketClient;
}
