package com.ridematch.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class JwtValidator {
    private final SecretKey key;
    private final long skewSeconds;

    public JwtValidator(String secret, long skewSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.skewSeconds = Math.max(0, skewSeconds);
    }

    public UserPrincipal validateAndExtract(String token) throws JwtException {
        try {
            var parser = Jwts.parserBuilder().setSigningKey(key);

            Claims claims = parser.build().parseClaimsJws(token).getBody();

            String username = claims.getSubject();
            String uid =
                    Optional.ofNullable(claims.get("uid", String.class))
                            .orElseThrow(() -> new JwtException("Missing uid claim"));
            String role = Optional.ofNullable(claims.get("role", String.class)).orElse("ROLE_USER");

            return new UserPrincipal(uid, username, Set.of(role));

        } catch (Exception exception) {
            log.error("An exception happened with messege: {}", exception.getMessage());
        }
        return null;
    }
}
