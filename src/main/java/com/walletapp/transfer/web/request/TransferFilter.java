package com.walletapp.transfer.web.request;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record TransferFilter(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
    Long fromAccountId,
    Long toAccountId) {}
