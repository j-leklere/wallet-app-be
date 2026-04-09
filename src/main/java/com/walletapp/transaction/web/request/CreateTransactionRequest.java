package com.walletapp.transaction.web.request;

import com.walletapp.shared.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest(
    @NotNull(message = "El tipo es requerido") TransactionType type,
    @NotNull(message = "El monto es requerido") @Positive BigDecimal originalAmount,
    @NotNull(message = "La moneda es requerida") Long originalCurrencyId,
    @NotNull(message = "La fecha es requerida")
        @PastOrPresent(message = "La fecha no puede ser futura")
        LocalDate date,
    String description,
    @NotNull(message = "La cuenta es requerida") Long accountId,
    Long categoryId) {}
