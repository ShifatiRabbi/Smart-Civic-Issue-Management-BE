package com.sr.smart_civic_platform.security.jwt;

import com.sr.smart_civic_platform.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

/*
 * Purpose:
 * JWT access/refresh token তৈরি করা এবং পরে validate/parse করা —
 * একটাই জায়গা থেকে, যাতে token logic ছড়িয়ে না থাকে।
 *
 * Why one class for both access & refresh:
 * দুইটার structure একই (claims + expiration), শুধু expiration duration
 * আলাদা। তাই duplicate code এড়াতে দুইটা generic method বানানো হয়েছে,
 * generateAccessToken() আর generateRefreshToken() শুধু duration পাস করে।
 *
 * Security:
 * Secret key .env থেকে আসছে (JWT_SECRET) — code এ hardcoded না।
 * HMAC-SHA256 algorithm ব্যবহার হচ্ছে।
 */
@Component
public class JwtUtil {

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final SecretKey secretKey;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpirationMs,
            @Value("${jwt.refresh-expiration}") long refreshExpirationMs) {
        // Why UTF-8 bytes: HMAC key needs raw bytes; secret string from .env is UTF-8.
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateAccessToken(User user) {
        return buildToken(user, accessExpirationMs);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, refreshExpirationMs);
    }

    public long getAccessExpirationMs() {
        return accessExpirationMs;
    }

    private String buildToken(User user, long expirationMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(user.getId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // ---- Parsing / validation (used by future JWT filter in STEP-02e) ----

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

     public String extractType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }


    /*
     * Purpose:
     * Token parse করা এবং সাথে সাথে verify করা যে এইটা "refresh" type token।
     *
     * Why throw JwtException here (not BusinessException):
     * JwtUtil হলো low-level utility — HTTP concern (status code) জানা
     * উচিত না। Service layer এই exception catch করে BusinessException
     * এ map করবে (401/403 সহ)।
     */
    public String validateAndExtractUserIdFromRefreshToken(String token) {
        Claims claims = parseClaims(token); // throws JwtException if invalid/expired/tampered

        String type = claims.get("type", String.class);
        if (!TYPE_REFRESH.equals(type)) {
            throw new JwtException("Token is not a refresh token");
        }

        return claims.getSubject();
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(parseClaims(token));
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}