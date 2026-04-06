package com.walletapp.currency.web.response;

public record CurrencyResponse(Long id, String code, String name, String symbol, boolean active) {}
