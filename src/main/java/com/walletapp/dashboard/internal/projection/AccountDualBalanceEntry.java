package com.walletapp.dashboard.internal.projection;

import java.math.BigDecimal;

public record AccountDualBalanceEntry(
    Long accountId, BigDecimal nativeBalance, BigDecimal arsBalance) {}
