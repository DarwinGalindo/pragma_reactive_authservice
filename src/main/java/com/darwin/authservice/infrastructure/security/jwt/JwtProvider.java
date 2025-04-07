package com.darwin.authservice.infrastructure.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {
    private static final String ROLES_CLAIM = "roles";

    private Key key;
    private JwtParser jwtParser;
    private long tokenValidityInMilliseconds;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.duration}")
    private long duration;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);

        key = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        tokenValidityInMilliseconds = 1000 * duration;
    }

    public String generateToken(Authentication authentication) {
        long now = new Date().getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(ROLES_CLAIM, roles)
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error("Token validation error {}", e.getMessage());
        }
        return false;
    }

    public String getUsernameFromToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }

    public List<String> getRolesFromToken(String token) {
        Claims claims = jwtParser
                .parseClaimsJws(token)
                .getBody();

        Object roles = claims.get(ROLES_CLAIM);
        if (roles instanceof List<?>) {
            return ((List<?>) roles).stream()
                    .map(String::valueOf)
                    .toList();
        }
        return List.of();
    }
}
