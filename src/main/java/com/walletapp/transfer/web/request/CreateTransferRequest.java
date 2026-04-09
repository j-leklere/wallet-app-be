package com.walletapp.transfer.web.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransferRequest(
    @NotNull(message = "El monto es requerido") @Positive BigDecimal fromAmount,
    @NotNull(message = "La fecha es requerida")
        @PastOrPresent(message = "La fecha no puede ser futura")
        LocalDate date,
    String description,
    @NotNull(message = "La cuenta origen es requerida") Long fromAccountId,
    @NotNull(message = "La cuenta destino es requerida") Long toAccountId) {}
