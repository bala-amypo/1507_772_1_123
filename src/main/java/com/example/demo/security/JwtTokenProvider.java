package com.example.demo.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final String JWT_SECRET = "mySecretKey"; // use a secure secret
    private final long JWT_EXPIRATION_MS = 86400000; // 1 day

    // Generate token with email only
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        return createToken(claims);
    }

    // Generate token with userId, email, role
    public String generateToken(Long userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("role", role);
        return createToken(claims);
    }

    // Common method to create token
    private String createToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    // Get email from token
    public String getEmailFromToken(String token) {
        return getClaims(token).get("email", String.class);
    }

    // Get userId from token
    public Long getUserIdFromToken(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    // Get role from token
    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    // Get all claims from token
    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(JWT_SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            return ex.getClaims();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new RuntimeException("Invalid JWT token");
        }
    }
}
