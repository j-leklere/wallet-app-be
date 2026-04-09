package com.walletapp.category.internal.service;

import com.walletapp.category.internal.domain.Category;
import com.walletapp.category.internal.domain.CategoryConstants;
import com.walletapp.category.internal.mapper.CategoryMapper;
import com.walletapp.category.internal.repository.CategoryRepository;
import com.walletapp.category.web.request.CreateCategoryRequest;
import com.walletapp.category.web.request.UpdateCategoryRequest;
import com.walletapp.category.web.response.CategoryResponse;
import com.walletapp.shared.exception.BadRequestException;
import com.walletapp.shared.exception.EntityInUseException;
import com.walletapp.shared.exception.ResourceNotFoundException;
import com.walletapp.transaction.internal.repository.TransactionRepository;
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
  private final TransactionRepository transactionRepository;

  public List<CategoryResponse> findAllByUser(Long userId) {
    return categoryRepository.findAllByUserId(userId).stream()
        .map(categoryMapper::toResponse)
        .toList();
  }

  public CategoryResponse findById(Long id, Long userId) {
    return categoryMapper.toResponse(getOrThrow(id, userId));
  }

  public CategoryResponse create(CreateCategoryRequest request, Long userId) {
    validateIconKey(request.iconKey());
    validateColorKey(request.colorKey());

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

    Category category =
        Category.create(request.name(), request.iconKey(), request.colorKey(), user);
    return categoryMapper.toResponse(categoryRepository.save(category));
  }

  public CategoryResponse update(Long id, Long userId, UpdateCategoryRequest request) {
    Category category = getOrThrow(id, userId);
    if (request.name() != null) category.setName(request.name());
    if (request.active() != null) category.setActive(request.active());
    if (request.iconKey() != null) {
      validateIconKey(request.iconKey());
      category.setIconKey(request.iconKey());
    }
    if (request.colorKey() != null) {
      validateColorKey(request.colorKey());
      category.setColorKey(request.colorKey());
    }
    return categoryMapper.toResponse(categoryRepository.save(category));
  }

  private void validateIconKey(String iconKey) {
    if (!CategoryConstants.ALLOWED_ICONS.contains(iconKey)) {
      throw new BadRequestException("iconKey inválido: " + iconKey);
    }
  }

  private void validateColorKey(String colorKey) {
    if (!CategoryConstants.ALLOWED_COLORS.contains(colorKey)) {
      throw new BadRequestException("colorKey inválido: " + colorKey);
    }
  }

  public void delete(Long id, Long userId) {
    Category category = getOrThrow(id, userId);
    if (transactionRepository.existsByCategoryId(id)) {
      throw new EntityInUseException(
          "No se puede eliminar la categoría porque tiene transacciones asociadas. Desactivala en su lugar.");
    }
    categoryRepository.delete(category);
  }

  private Category getOrThrow(Long id, Long userId) {
    return categoryRepository
        .findByIdAndUserId(id, userId)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
  }
}
