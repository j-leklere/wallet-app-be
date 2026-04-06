package com.walletapp.auth.web.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
  private String token;
  private final String type = "Bearer";
  private String username;
  private Long userId;
  private long expiresIn;
}
