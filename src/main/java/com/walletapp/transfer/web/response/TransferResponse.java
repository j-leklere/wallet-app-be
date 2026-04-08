package com.walletapp.transfer.web.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransferResponse(
    Long id,
    BigDecimal fromAmount,
    Long fromCurrencyId,
    String fromCurrencyCode,
    String fromCurrencySymbol,
    BigDecimal toAmount,
    Long toCurrencyId,
    String toCurrencyCode,
    String toCurrencySymbol,
    BigDecimal exchangeRate,
    LocalDate date,
    String description,
    Long fromAccountId,
    String fromAccountName,
    Long toAccountId,
    String toAccountName) {}
