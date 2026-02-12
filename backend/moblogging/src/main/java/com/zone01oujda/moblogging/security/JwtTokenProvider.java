package com.zone01oujda.moblogging.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.zone01oujda.moblogging.entity.User;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtAccessExpiration;

    private SecretKey secretKey ;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccesToken(User user) {
        Map<String,Object> claims = new HashMap<>();
        claims.put("fnm", user.getFirstName() + user.getLastName());
        claims.put("UserId", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        return Jwts.builder()
        .setClaims(claims)
        .setSubject(user.getUsername())
        .setIssuer("moblogging")
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtAccessExpiration))
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    public String getUsernameIfValid(String token) {
        try {
            return getUsername(token);
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    public Long getUserId(String token) {
        Object val = Jwts.parserBuilder().setSigningKey(secretKey).build()
            .parseClaimsJws(token)
            .getBody()
            .get("UserId");
        if (val instanceof Integer) {
            return ((Integer) val).longValue();
        }
        if (val instanceof Long) {
            return (Long) val;
        }
        if (val instanceof String) {
            try {
                return Long.parseLong((String) val);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

}
