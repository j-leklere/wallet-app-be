package com.walletapp.transfer.web.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransferRequest(
    @NotNull(message = "From amount is required") @Positive BigDecimal fromAmount,
    @NotNull(message = "From currency is required") Long fromCurrencyId,
    @NotNull(message = "To amount is required") @Positive BigDecimal toAmount,
    @NotNull(message = "To currency is required") Long toCurrencyId,
    @NotNull(message = "Exchange rate is required") @Positive BigDecimal exchangeRate,
    @NotNull(message = "Date is required") LocalDate date,
    String description,
    @NotNull(message = "From account is required") Long fromAccountId,
    @NotNull(message = "To account is required") Long toAccountId) {}
