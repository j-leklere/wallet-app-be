package com.walletapp.dashboard.internal.projection;

import java.math.BigDecimal;

public record CategoryExpenseEntry(String categoryName, BigDecimal total, String colorKey) {}
