package com.walletapp.recurringtransaction.web.response;

import com.walletapp.recurringtransaction.internal.domain.RecurringFrequency;
import com.walletapp.shared.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RecurringTransactionResponse(
    Long id,
    TransactionType type,
    BigDecimal originalAmount,
    Long originalCurrencyId,
    String originalCurrencyCode,
    String originalCurrencySymbol,
    RecurringFrequency frequency,
    LocalDate nextExecutionDate,
    LocalDate endDate,
    String description,
    boolean active,
    Long accountId,
    Long categoryId) {}
