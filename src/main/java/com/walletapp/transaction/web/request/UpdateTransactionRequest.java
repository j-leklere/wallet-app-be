package com.walletapp.transaction.web.request;

import com.walletapp.shared.TransactionType;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTransactionRequest(
    TransactionType type,
    @Positive BigDecimal originalAmount,
    Long originalCurrencyId,
    LocalDate date,
    String description,
    Long accountId,
    Long categoryId) {}
