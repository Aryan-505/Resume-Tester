package com.aryan.resumeai.security.jwt;

import com.aryan.resumeai.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey =
                Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateAccessToken(User user) {

        Map<String, Object> claims =
                new HashMap<>();

        claims.put("role",
                user.getRole().name());

        claims.put("userId",
                user.getId());

        return createToken(
                claims,
                user.getEmail(),
                accessTokenExpiration
        );
    }

    public String generateRefreshToken(User user) {

        return createToken(
                new HashMap<>(),
                user.getEmail(),
                refreshTokenExpiration
        );
    }

    private String createToken(
            Map<String, Object> claims,
            String subject,
            Long expiration
    ) {

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration
                        )
                )
       .signWith(secretKey)
                .compact();
    }

    public String extractEmail(
            String token
    ) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    public Date extractExpiration(
            String token
    ) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        Claims claims =
                extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(
            String token
    ) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(
            String token
    ) {

        return extractExpiration(token)
                .before(new Date());
    }

    public boolean isTokenValid(
            String token,
            String email
    ) {

        String tokenEmail =
                extractEmail(token);

        return tokenEmail.equals(email)
                && !isTokenExpired(token);
    }
}