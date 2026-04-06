package com.walletapp.transaction.web.request;

import com.walletapp.shared.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest(
    @NotNull(message = "Type is required") TransactionType type,
    @NotNull(message = "Original amount is required") @Positive BigDecimal originalAmount,
    @NotNull(message = "Original currency is required") Long originalCurrencyId,
    @NotNull(message = "Reference amount is required") @Positive BigDecimal referenceAmount,
    @NotNull(message = "Reference currency is required") Long referenceCurrencyId,
    @NotNull(message = "Exchange rate is required") @Positive BigDecimal exchangeRate,
    @NotNull(message = "Date is required") LocalDate date,
    String description,
    @NotNull(message = "Account is required") Long accountId,
    Long categoryId) {}
