package com.walletapp.auth.internal.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtUtils {

  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Value("${app.jwt.expiration-ms}")
  private long jwtExpirationMs;

  private Key key() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(String username, Long userId) {
    return Jwts.builder()
        .setSubject(username)
        .claim("userId", userId)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String getUsernameFromToken(String token) {
    return parseClaims(token).getSubject();
  }

  public Long getUserIdFromToken(String token) {
    return parseClaims(token).get("userId", Long.class);
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
      return true;
    } catch (MalformedJwtException e) {
      log.warn("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.warn("JWT token expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.warn("Unsupported JWT token: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.warn("JWT claims empty: {}", e.getMessage());
    }
    return false;
  }

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
  }
}
