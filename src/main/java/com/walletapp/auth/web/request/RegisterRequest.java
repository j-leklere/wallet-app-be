package com.walletapp.auth.web.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank(message = "El nombre de usuario es requerido") String username,
    @Email(message = "El email no es válido") @NotBlank(message = "El email es requerido")
        String email,
    @NotBlank(message = "La contraseña es requerida") String password) {}
