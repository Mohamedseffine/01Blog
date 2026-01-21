package com.zone01oujda.moblogging.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.zone01oujda.moblogging.util.response.ApiResponse;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitingFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/ws");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String key = resolveKey(request);
        Bucket bucket = buckets.computeIfAbsent(key, this::createBucket);
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiResponse<Object> body = new ApiResponse<>(false, "Too many requests");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private Bucket createBucket(String key) {
        Bandwidth limit = resolveLimitForKey(key);
        return Bucket.builder().addLimit(limit).build();
    }

    private Bandwidth resolveLimitForKey(String key) {
        if (key.contains(":auth:login")) {
            return Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
        }
        if (key.contains(":auth:register")) {
            return Bandwidth.classic(3, Refill.intervally(3, Duration.ofMinutes(1)));
        }
        if (key.contains(":auth:refresh") || key.contains(":auth:logout")) {
            return Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
        }
        return Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
    }

    private String resolveKey(HttpServletRequest request) {
        String path = request.getServletPath();
        String bucketName = "default";
        if (path.startsWith("/auth/login")) {
            bucketName = "auth:login";
        } else if (path.startsWith("/auth/register")) {
            bucketName = "auth:register";
        } else if (path.startsWith("/auth/refresh")) {
            bucketName = "auth:refresh";
        } else if (path.startsWith("/auth/logout")) {
            bucketName = "auth:logout";
        }
        String ip = resolveClientIp(request);
        return ip + ":" + bucketName;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
