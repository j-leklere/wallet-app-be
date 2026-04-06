package com.walletapp.category.internal.mapper;

import com.walletapp.category.internal.domain.Category;
import com.walletapp.category.web.response.CategoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  CategoryResponse toResponse(Category category);
}
