package com.walletapp.auth.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank(message = "Username is required") String username,
    @Email(message = "Email must be valid") @NotBlank(message = "Email is required") String email,
    @NotBlank(message = "Password is required") String password) {}
