package com.walletapp.category.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
    @NotBlank(message = "Name is required") @Size(max = 100) String name) {}
