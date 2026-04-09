package com.walletapp.category.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
    @NotBlank(message = "El nombre es requerido") @Size(max = 100) String name,
    @NotBlank(message = "El ícono es requerido") @Size(max = 50) String iconKey,
    @NotBlank(message = "El color es requerido") @Size(max = 20) String colorKey) {}
