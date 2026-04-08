package com.walletapp.transfer.web.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransferRequest(
    @NotNull(message = "From amount is required") @Positive BigDecimal fromAmount,
    @NotNull(message = "Date is required") LocalDate date,
    String description,
    @NotNull(message = "From account is required") Long fromAccountId,
    @NotNull(message = "To account is required") Long toAccountId) {}
