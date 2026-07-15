package com.yourdomain.ecommerce.security;

import com.yourdomain.ecommerce.config.properties.JwtProperties;
import com.yourdomain.ecommerce.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties props;
    private SecretKey signingKey;

    @PostConstruct
    void init() {
        this.signingKey = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long userId, String username, Collection<String> roles) {
        return buildToken(userId, username, roles, props.getAccessTokenExpirationMs());
    }

    public String generateRefreshToken(Long userId, String username) {
        return buildToken(userId, username, Set.of(), props.getRefreshTokenExpirationMs());
    }

    private String buildToken(Long userId, String username, Collection<String> roles, long ttlMs) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ttlMs);
        return Jwts.builder()
                .issuer(props.getIssuer())
                .subject(username)
                .claim(SecurityConstants.CLAIM_USER_ID, userId)
                .claim(SecurityConstants.CLAIM_ROLES, roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception ex) {
            log.debug("Invalid JWT: {}", ex.getMessage());
            return false;
        }
    }

    public Set<String> extractRoles(Claims claims) {
        Object roles = claims.get(SecurityConstants.CLAIM_ROLES);
        if (roles instanceof Collection<?> c) {
            return c.stream().map(Object::toString).collect(Collectors.toSet());
        }
        return Set.of();
    }
}
