package com.walletapp.account.web.request;

import com.walletapp.account.internal.domain.AccountType;
import jakarta.validation.constraints.Size;

public record UpdateAccountRequest(
    @Size(max = 100) String name, AccountType type, Boolean active) {}
