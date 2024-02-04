package com.inventorymanagement.imbatch.config.security;

import com.inventorymanagement.imbatch.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtUtil {

  private final static String TOKEN_HEADER = "Authorization";
  private final static String TOKEN_PREFIX = "Bearer ";
  @Value("${jwt.secretKey}")
  private String secret_key;
  private JwtParser jwtParser;

  @PostConstruct
  protected void init() {
    this.jwtParser = Jwts
            .parserBuilder()
            .setSigningKey(secret_key.getBytes())
            .build();
  }

  public String createToken(User user) {
    Claims claims = Jwts.claims().setSubject(user.getClerkId());
    claims.put("firstName", user.getFirstName());
    claims.put("lastName", user.getLastName());
    return Jwts.builder()
            .setClaims(claims)
            .setExpiration(new Date(String.valueOf(LocalDateTime.now().plusDays(10))))
            .signWith(SignatureAlgorithm.HS256, secret_key.getBytes())
            .compact();
  }

  private Claims parseJwtClaims(String token) {
    return jwtParser.parseClaimsJws(token).getBody();
  }

  public Claims resolveClaims(HttpServletRequest req) {
    try {
      String token = resolveToken(req);
      if (token != null) {
        return parseJwtClaims(token);
      }
      return null;
    } catch (ExpiredJwtException ex) {
      req.setAttribute("expired", ex.getMessage());
      throw ex;
    } catch (Exception ex) {
      req.setAttribute("invalid", ex.getMessage());
      throw ex;
    }
  }

  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(TOKEN_HEADER);
    if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
      return bearerToken.substring(TOKEN_PREFIX.length());
    }
    return null;
  }

  public boolean validateClaims(Claims claims) throws AuthenticationException {
    try {
      // TODO: FIX THIS
      //      return claims.getExpiration().after(new Date());
      return true;
    } catch (Exception e) {
      throw e;
    }
  }
}
