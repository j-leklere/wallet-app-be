package com.walletapp.account.web.response;

import com.walletapp.account.internal.domain.AccountType;

public record AccountResponse(
    Long id,
    String name,
    AccountType type,
    Long currencyId,
    String currencyCode,
    String currencySymbol,
    boolean active) {}
