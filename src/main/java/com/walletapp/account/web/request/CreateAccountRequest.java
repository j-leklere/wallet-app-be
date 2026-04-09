package com.walletapp.account.web.request;

import com.walletapp.account.internal.domain.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
    @NotBlank(message = "El nombre es requerido") @Size(max = 100) String name,
    @NotNull(message = "El tipo es requerido") AccountType type,
    @NotNull(message = "La moneda es requerida") Long currencyId) {}
