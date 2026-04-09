package com.walletapp.user.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @Size(max = 50) String username,
    @Email(message = "El email no es válido") String email,
    String currentPassword,
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String newPassword) {}
