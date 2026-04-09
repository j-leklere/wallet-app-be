package com.walletapp.category.web.response;

public record CategoryResponse(
    Long id, String name, String iconKey, String colorKey, boolean active) {}
