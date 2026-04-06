package com.walletapp.category.internal.service;

import com.walletapp.category.internal.domain.Category;
import com.walletapp.category.internal.mapper.CategoryMapper;
import com.walletapp.category.internal.repository.CategoryRepository;
import com.walletapp.category.web.request.CreateCategoryRequest;
import com.walletapp.category.web.request.UpdateCategoryRequest;
import com.walletapp.category.web.response.CategoryResponse;
import com.walletapp.shared.exception.ResourceNotFoundException;
import com.walletapp.user.internal.domain.User;
import com.walletapp.user.internal.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;
  private final CategoryMapper categoryMapper;

  public List<CategoryResponse> findAllByUser(Long userId) {
    return categoryRepository.findAllByUserId(userId).stream()
        .map(categoryMapper::toResponse)
        .toList();
  }

  public CategoryResponse findById(Long id, Long userId) {
    return categoryMapper.toResponse(getOrThrow(id, userId));
  }

  public CategoryResponse create(CreateCategoryRequest request, Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

    Category category = Category.create(request.name(), user);
    return categoryMapper.toResponse(categoryRepository.save(category));
  }

  public CategoryResponse update(Long id, Long userId, UpdateCategoryRequest request) {
    Category category = getOrThrow(id, userId);
    if (request.name() != null) category.setName(request.name());
    if (request.active() != null) category.setActive(request.active());
    return categoryMapper.toResponse(categoryRepository.save(category));
  }

  public void delete(Long id, Long userId) {
    Category category = getOrThrow(id, userId);
    categoryRepository.delete(category);
  }

  private Category getOrThrow(Long id, Long userId) {
    return categoryRepository
        .findByIdAndUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
  }
}
