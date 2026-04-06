package com.walletapp.transaction.web.request;

import com.walletapp.shared.TransactionType;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record TransactionFilter(
    TransactionType type,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
    Long accountId,
    Long categoryId) {}
