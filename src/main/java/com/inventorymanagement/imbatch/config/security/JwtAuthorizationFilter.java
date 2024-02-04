package com.inventorymanagement.imbatch.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventorymanagement.imbatch.exception.AuthorizationException;
import com.inventorymanagement.imbatch.models.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final ObjectMapper mapper;
  private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    Map<String, Object> errorDetails = new HashMap<>();

    try {
      String accessToken = jwtUtil.resolveToken(request);
      if (accessToken == null) {
        filterChain.doFilter(request, response);
        return;
      }

      Claims claims = jwtUtil.resolveClaims(request);

      if (claims != null & jwtUtil.validateClaims(claims)) {
        User usr = getUser(claims);

        List<GrantedAuthority> authorities = usr.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        //        printColored("Authorities: " + authorities, ConsoleFormatter.Color.PURPLE);
        Authentication authentication =  new UsernamePasswordAuthenticationToken(usr, "", authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }

    } catch (Exception e) {
      throw new AuthorizationException("details: " + e.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  private User getUser(Claims claims) {
    String clerkId = (String) claims.get("clerkId");
    String firstName = (String) claims.get("firstName");
    String lastName = (String) claims.get("lastName");
    String username = (String) claims.get("username");
    List<String> roles = (List<String>) claims.get("roles");
    String imageUrl = "";
    return new User(clerkId, firstName, lastName, username, imageUrl, roles);
  }

}