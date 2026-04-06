package com.walletapp.category.web.request;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(@Size(max = 100) String name, Boolean active) {}
