package com.walletapp.auth.internal.service;

import com.walletapp.auth.internal.utils.JwtUtils;
import com.walletapp.auth.web.request.LoginRequest;
import com.walletapp.auth.web.request.RegisterRequest;
import com.walletapp.auth.web.response.AuthResponse;
import com.walletapp.shared.exception.InvalidCredentialsException;
import com.walletapp.shared.exception.UserAlreadyExistsException;
import com.walletapp.user.internal.domain.User;
import com.walletapp.user.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

  private final JwtUtils jwtUtils;
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.jwt.expiration-ms}")
  private long jwtExpirationMs;

  public AuthResponse login(LoginRequest request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.email(), request.password()));
    } catch (AuthenticationException e) {
      log.warn("Login failed for email: {}", request.email());
      throw new InvalidCredentialsException("Invalid email or password");
    }

    User user =
        userRepository
            .findByEmail(request.email())
            .orElseThrow(() -> new InvalidCredentialsException("User not found"));

    String token = jwtUtils.generateToken(user.getUsername(), user.getId());
    log.info("User logged in: {}", user.getUsername());

    return buildAuthResponse(token, user);
  }

  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByUsername(request.username())) {
      throw new UserAlreadyExistsException("Username already exists");
    }
    if (userRepository.existsByEmail(request.email())) {
      throw new UserAlreadyExistsException("Email already exists");
    }

    User user =
        User.create(
            request.username(), request.email(), passwordEncoder.encode(request.password()));

    User saved = userRepository.save(user);
    String token = jwtUtils.generateToken(saved.getUsername(), saved.getId());
    log.info("User registered: {}", saved.getUsername());

    return buildAuthResponse(token, saved);
  }

  public User getCurrentUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  public Long getCurrentUserId() {
    return getCurrentUser().getId();
  }

  private AuthResponse buildAuthResponse(String token, User user) {
    return AuthResponse.builder()
        .token(token)
        .username(user.getUsername())
        .userId(user.getId())
        .expiresIn(jwtExpirationMs / 1000)
        .build();
  }
}
