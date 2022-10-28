package com.schoolroyale.aurora.auth.token;

import com.schoolroyale.aurora.auth.ApiUser;
import com.schoolroyale.aurora.time.Time;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {

    private @Value("${jwt.secret}") String key;
    private @Value("${jwt.expiration}") long expiration;

    private Key securityKey;

    @PostConstruct
    public void init() {
        this.securityKey = Keys.hmacShaKeyFor(key.getBytes());
    }

    public Claims claimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String usernameFromToken(String token) {
        return claimsFromToken(token).getSubject();
    }

    public Date getExpirationDateFromToken(String token) {
        return claimsFromToken(token).getExpiration();
    }

    private boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    public String createToken(ApiUser apiUser) {
        var claims = new HashMap<String, Object>();
        claims.put("role", apiUser.role());

        return doGenerateToken(claims, apiUser.username());
    }

    private String doGenerateToken(Map<String, Object> claims, String username) {
        var createdDate = Time.now();
        var expirationDate = Time.when(Duration.ofMillis(expiration));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(securityKey)
                .compact();
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token) /* && !isTokenBlackListed(token)? */;
    }

}
