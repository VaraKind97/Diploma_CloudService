package ru.netology.cloudservice.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {
    private final Key secretKey;
    private final long expiration = 86400000L;

    public JwtTokenProvider(@Value("${app.jwt.secret}")
                            String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createToken(String u, Map<String, Object> c) {
        return Jwts.builder()
                .setClaims(c)
                .setSubject(u)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String t) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(t);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaimsFromToken(String t) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey).build()
                .parseClaimsJws(t).getBody();
    }
}