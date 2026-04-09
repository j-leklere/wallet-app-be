package com.walletapp.recurringtransaction.web.request;

import com.walletapp.recurringtransaction.internal.domain.RecurringFrequency;
import com.walletapp.shared.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateRecurringTransactionRequest(
    @NotNull(message = "El tipo es requerido") TransactionType type,
    @NotNull(message = "El monto es requerido") @Positive BigDecimal originalAmount,
    @NotNull(message = "La moneda es requerida") Long originalCurrencyId,
    @NotNull(message = "La frecuencia es requerida") RecurringFrequency frequency,
    @NotNull(message = "La fecha de próxima ejecución es requerida") LocalDate nextExecutionDate,
    LocalDate endDate,
    String description,
    @NotNull(message = "La cuenta es requerida") Long accountId,
    Long categoryId) {}
