
package com.payflow.service;

import com.payflow.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    // Generate JWT token for a user
    public String generateToken(User user) {

        Date issuedAt = new Date();
        Date expiration = new Date(
                issuedAt.getTime() + expirationMs
        );

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    // Extract email from JWT token
    public String extractUsername(String token) {

        return extractClaims(token).getSubject();
    }

    // Validate token and check whether it belongs to the user
    public boolean isTokenValid(String token, User user) {

        try {
            String username = extractUsername(token);

            return username.equals(user.getEmail());

        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    // Extract all claims after verifying token signature
    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
