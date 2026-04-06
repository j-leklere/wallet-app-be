package com.walletapp.transaction.web.response;

import com.walletapp.shared.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
    Long id,
    TransactionType type,
    BigDecimal originalAmount,
    Long originalCurrencyId,
    String originalCurrencyCode,
    String originalCurrencySymbol,
    BigDecimal referenceAmount,
    Long referenceCurrencyId,
    String referenceCurrencyCode,
    String referenceCurrencySymbol,
    BigDecimal exchangeRate,
    LocalDate date,
    String description,
    Long accountId,
    Long categoryId) {}
