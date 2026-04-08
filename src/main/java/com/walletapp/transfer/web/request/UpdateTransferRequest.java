package com.walletapp.transfer.web.request;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateTransferRequest(
    @Positive BigDecimal fromAmount,
    LocalDate date,
    String description,
    Long fromAccountId,
    Long toAccountId) {}
