package com.walletapp.user.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @Size(max = 50) String username, @Email(message = "Email must be valid") String email) {}
