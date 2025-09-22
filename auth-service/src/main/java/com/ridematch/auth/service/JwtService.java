package com.ridematch.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expirationMinutes}") long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(UserDetails user, String userId) {
        Instant now = Instant.now();
        String role =
                user.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse("ROLE_USER");

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
                .claim("role", role)
                .claim("uid", userId)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return parseAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails user) {
        String username = extractUsername(token);
        return username != null && username.equals(user.getUsername()) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        Date exp = parseAllClaims(token).getExpiration();
        return exp != null && exp.before(new Date());
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
