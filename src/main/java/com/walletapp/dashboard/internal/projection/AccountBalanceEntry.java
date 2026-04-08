package com.walletapp.dashboard.internal.projection;

import java.math.BigDecimal;

public record AccountBalanceEntry(Long accountId, BigDecimal balance) {}
