package com.walletapp.recurringtransaction.web.request;

import com.walletapp.recurringtransaction.internal.domain.RecurringFrequency;
import com.walletapp.shared.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateRecurringTransactionRequest(
    @NotNull(message = "Type is required") TransactionType type,
    @NotNull(message = "Original amount is required") @Positive BigDecimal originalAmount,
    @NotNull(message = "Original currency is required") Long originalCurrencyId,
    @NotNull(message = "Frequency is required") RecurringFrequency frequency,
    @NotNull(message = "Next execution date is required") LocalDate nextExecutionDate,
    LocalDate endDate,
    String description,
    @NotNull(message = "Account is required") Long accountId,
    Long categoryId) {}
