package com.walletapp.config;

import com.walletapp.auth.internal.utils.JwtUtils;
import com.walletapp.user.internal.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

  private final JwtUtils jwtUtils;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return request.getRequestURI().startsWith("/api/auth/");
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    try {
      String jwt = extractToken(request);

      if (jwt != null) {
        if (jwtUtils.validateToken(jwt)) {
          String username = jwtUtils.getUsernameFromToken(jwt);
          Long userId = jwtUtils.getUserIdFromToken(jwt);

          User user = User.builder().id(userId).username(username).build();

          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(user, null, List.of());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
          sendUnauthorized(response);
          return;
        }
      }
    } catch (Exception e) {
      log.warn("JWT processing failed for {}: {}", request.getRequestURI(), e.getMessage());
      sendUnauthorized(response);
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String extractToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    return null;
  }

  private void sendUnauthorized(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
  }
}
