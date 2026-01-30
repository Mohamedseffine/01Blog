package com.zone01oujda.moblogging.notification.websocket;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.zone01oujda.moblogging.security.JwtTokenProvider;

@Component
public class JwtHandshakeHandler extends DefaultHandshakeHandler {
    private final JwtTokenProvider jwtprovider;

    public JwtHandshakeHandler(JwtTokenProvider jwtprovider) {
        this.jwtprovider = jwtprovider;
    }
    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        String token = (String) attributes.get("token");
        if (token == null || token.isBlank()) {
            return null;
        }

        if (!jwtprovider.validateToken(token)) {
            return null;
        }
        String username = jwtprovider.getUsernameIfValid(token);
        if (username == null) {
            return null;
        }

        return () -> username;
    }
}
