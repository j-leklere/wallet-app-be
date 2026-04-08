package com.walletapp.dashboard.internal.projection;

import com.walletapp.shared.TransactionType;
import java.math.BigDecimal;

public record MonthlyAmountEntry(
    Integer year, Integer month, TransactionType type, BigDecimal total) {}
