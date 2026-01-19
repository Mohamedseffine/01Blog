# WebSocket Real-Time Notifications

## Overview

This project implements WebSocket support for real-time notifications using Spring Boot with STOMP (Simple Text Oriented Messaging Protocol) over WebSocket. Users receive instant notifications without needing to poll the server.

## Architecture

### Server-Side Components

#### 1. **WebSocketConfig** (`notification/websocket/WebSocketConfig.java`)
- Configures STOMP message broker
- Registers WebSocket endpoints
- Sets up application destination prefixes

**Key Features:**
- In-memory message broker for development/testing
- SockJS fallback for browsers without WebSocket support
- Configurable CORS origins

#### 2. **NotificationSocketHandler** (`notification/websocket/NotificationSocketHandler.java`)
- Handles incoming WebSocket messages
- Manages user connection tracking
- Provides methods to send notifications to users

**Key Methods:**
- `sendNotificationToUser(userId, notification)` - Send to specific user
- `broadcastNotification(notification)` - Send to all connected users
- `isUserConnected(userId)` - Check user connection status
- `getTotalConnectedUsers()` - Get online users count

#### 3. **NotificationEventPublisher** (`notification/websocket/NotificationEventPublisher.java`)
- High-level service for publishing notifications
- Simplifies notification delivery logic
- Provides status checking methods

**Key Methods:**
- `publishToUser(userId, notification)` - Publish to single user
- `publishToAll(notification)` - Broadcast to all
- `publishToUsers(userIds[], notification)` - Publish to multiple users
- `isUserOnline(userId)` - Check if user is online

#### 4. **NotificationMessage** (`notification/websocket/NotificationMessage.java`)
- Data transfer object for WebSocket messages
- Wraps notification with metadata
- Includes timestamp and message type

#### 5. **NotificationController** (`notification/controller/NotificationController.java`)
- Enhanced with WebSocket awareness
- Provides endpoints for notification status
- System statistics endpoint

## Client-Side Components

### JavaScript WebSocket Client (`frontend/websocket-client.js`)

**Features:**
- Automatic reconnection with exponential backoff
- Event-based callback system
- SockJS + STOMP protocol support

**Usage:**
```javascript
// Create client instance
const wsClient = new NotificationWebSocketClient(userId);

// Register callbacks
wsClient.on('onConnected', () => console.log('Connected'));
wsClient.on('onNotification', (notification) => {
    console.log('Received:', notification);
});

// Connect to server
wsClient.connect();

// Send notification
wsClient.sendNotification({
    message: 'Hello',
    type: 'INFO'
});

// Disconnect
wsClient.disconnect();
```

### HTML Example (`frontend/websocket-notifications.html`)

Complete example page with:
- Connection status indicator
- Notification message form
- Real-time notification display
- System status monitoring

## WebSocket Endpoints

### STOMP Destinations

**User-Specific Queue:**
```
/user/{userId}/queue/notifications
```
- Receives notifications targeted to specific user
- Private queue for user-only messages

**Broadcast Topic:**
```
/topic/notifications
```
- All connected users receive messages
- For system announcements

**Application Endpoints:**
```
/app/notifications/send        - Send notification
/app/users/connect            - Register user connection
/app/users/disconnect         - Register user disconnection
```

## REST API Endpoints

### Notification Management
```
GET    /notifications                    - Get all notifications (paginated)
GET    /notifications/unread             - Get unread notifications
PUT    /notifications/{id}/read          - Mark as read
DELETE /notifications/{id}               - Delete notification
PUT    /notifications/read-all           - Mark all as read
```

### WebSocket Status
```
GET    /notifications/status/{userId}    - Get user online status
GET    /notifications/system/stats       - Get system statistics
```

## Integration Guide

### 1. Add Maven Dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

### 2. Enable WebSocket in Application

WebSocket is automatically configured via `@Configuration` classes. No additional setup needed.

### 3. Inject NotificationEventPublisher

```java
@Service
public class MyService {
    private final NotificationEventPublisher eventPublisher;
    
    public MyService(NotificationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    public void notifyUser(Long userId) {
        NotificationDto notification = new NotificationDto();
        notification.setMessage("Your notification");
        eventPublisher.publishToUser(userId, notification);
    }
}
```

### 4. Client-Side Integration

```html
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
<script src="/path/to/websocket-client.js"></script>

<script>
    const wsClient = new NotificationWebSocketClient(userId);
    
    wsClient.on('onNotification', (notification) => {
        // Handle notification
        showNotification(notification.message);
    });
    
    wsClient.connect();
</script>
```

## Example Use Cases

### 1. Real-Time Comments
```java
// When a comment is created
Comment comment = commentService.create(dto);
NotificationDto notification = new NotificationDto();
notification.setMessage("New comment on your post");
notification.setContentId(comment.getPost().getId());
eventPublisher.publishToUser(post.getCreator().getId(), notification);
```

### 2. Follow Notifications
```java
// When user follows another
User follower = getCurrentUser();
NotificationDto notification = new NotificationDto();
notification.setMessage(follower.getUsername() + " followed you");
eventPublisher.publishToUser(followedUser.getId(), notification);
```

### 3. System Announcements
```java
// Broadcast to all users
NotificationDto announcement = new NotificationDto();
announcement.setMessage("System maintenance in 1 hour");
eventPublisher.publishToAll(announcement);
```

## Configuration

### Application Properties
```properties
# WebSocket configuration (optional, defaults are used if not specified)
server.servlet.context-path=/api
```

### Advanced Configuration

Modify `WebSocketConfig` for custom behavior:

```java
// Increase message broker settings
config.enableSimpleBroker("/topic", "/queue")
      .setRelayHost("localhost")
      .setRelayPort(61613);

// Configure allowed origins
registry.addEndpoint("/ws")
        .setAllowedOrigins("https://example.com")
        .withSockJS();
```

## Performance Considerations

### Connection Pooling
- In-memory broker suitable for single-server deployment
- For production with multiple servers, use external message broker (RabbitMQ, ActiveMQ)

### Scaling
```java
// For external broker, modify WebSocketConfig:
config.enableStompBrokerRelay("/topic", "/queue")
      .setRelayHost("rabbitmq.example.com")
      .setRelayPort(61613)
      .setClientLogin("guest")
      .setClientPasscode("guest");
```

## Security Considerations

### CORS Configuration
Currently allows all origins. For production:

```java
registry.addEndpoint("/ws")
        .setAllowedOrigins("https://yourdomain.com")
        .withSockJS();
```

### Authentication
Users are identified by userId. Ensure WebSocket clients are authenticated before connecting.

## Debugging

### Browser Console
```javascript
// Enable STOMP debugging
client.debug = function(str) {
    console.log('STOMP:', str);
};
```

### Server Logs
Enable DEBUG level for Spring WebSocket:
```properties
logging.level.org.springframework.web.socket=DEBUG
```

## Browser Compatibility

| Browser | WebSocket | SockJS |
|---------|-----------|--------|
| Chrome  | ✅        | ✅     |
| Firefox | ✅        | ✅     |
| Safari  | ✅        | ✅     |
| IE 11   | ❌        | ✅     |
| Edge    | ✅        | ✅     |

## Troubleshooting

### Connection Refused
- Check if WebSocket server is running on correct port
- Verify CORS configuration
- Check browser console for errors

### Messages Not Received
- Ensure client is subscribed to correct destination
- Verify server-side destination mapping
- Check if user is online via `/notifications/status/{userId}`

### Performance Issues
- Monitor number of concurrent connections
- Consider external message broker for high load
- Implement message batching if needed

## References

- [Spring WebSocket Documentation](https://spring.io/guides/gs/messaging-stomp-websocket/)
- [STOMP Protocol](https://stomp.github.io/)
- [SockJS](https://github.com/sockjs/sockjs-client)
