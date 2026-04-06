package com.walletapp.account.web.request;

import com.walletapp.account.internal.domain.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(
    @NotBlank(message = "Name is required") @Size(max = 100) String name,
    @NotNull(message = "Type is required") AccountType type,
    @NotNull(message = "Currency is required") Long currencyId) {}
