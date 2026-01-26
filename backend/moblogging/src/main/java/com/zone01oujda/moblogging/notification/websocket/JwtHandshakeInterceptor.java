package com.zone01oujda.moblogging.notification.websocket;

import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            String auth = servletRequest.getServletRequest().getHeader("Authorization");

            if (auth != null && auth.startsWith("Bearer ")) {
                attributes.put("token", auth.substring(7));
            } else {
                String token = servletRequest.getServletRequest().getParameter("token");
                if (token != null && !token.isBlank()) {
                    attributes.put("token", token);
                }
            }
        }
        return true;
    }

    // @Override
    // public void afterHandshake(...) {}


    @Override
    public void afterHandshake(ServerHttpRequest arg0, ServerHttpResponse arg1, WebSocketHandler arg2,
            @Nullable Exception arg3) {
        // no-op
    }
}
