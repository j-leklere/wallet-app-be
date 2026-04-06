package com.walletapp.recurringtransaction.web.request;

import com.walletapp.recurringtransaction.internal.domain.RecurringFrequency;
import com.walletapp.shared.TransactionType;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateRecurringTransactionRequest(
    TransactionType type,
    @Positive BigDecimal originalAmount,
    Long originalCurrencyId,
    RecurringFrequency frequency,
    LocalDate nextExecutionDate,
    LocalDate endDate,
    String description,
    Boolean active,
    Long accountId,
    Long categoryId) {}
