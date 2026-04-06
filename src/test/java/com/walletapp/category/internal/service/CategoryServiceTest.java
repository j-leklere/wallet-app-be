package com.walletapp.category.internal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock CategoryRepository categoryRepository;
  @Mock UserRepository userRepository;
  @Mock CategoryMapper categoryMapper;

  @InjectMocks CategoryService categoryService;

  @Test
  void findAllByUser_returnsMappedList() {
    Category cat = new Category();
    CategoryResponse response = new CategoryResponse(1L, "Food", true);
    when(categoryRepository.findAllByUserId(1L)).thenReturn(List.of(cat));
    when(categoryMapper.toResponse(cat)).thenReturn(response);

    List<CategoryResponse> result = categoryService.findAllByUser(1L);

    assertThat(result).containsExactly(response);
  }

  @Test
  void findById_returnsMappedCategory() {
    Category cat = new Category();
    CategoryResponse response = new CategoryResponse(1L, "Food", true);
    when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(cat));
    when(categoryMapper.toResponse(cat)).thenReturn(response);

    CategoryResponse result = categoryService.findById(1L, 1L);

    assertThat(result).isEqualTo(response);
  }

  @Test
  void findById_throwsWhenNotFound() {
    when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> categoryService.findById(1L, 1L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("1");
  }

  @Test
  void create_savesAndReturnsCategory() {
    User user = new User();
    Category saved = new Category();
    CategoryResponse response = new CategoryResponse(1L, "Food", true);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(categoryRepository.save(any(Category.class))).thenReturn(saved);
    when(categoryMapper.toResponse(saved)).thenReturn(response);

    CategoryResponse result = categoryService.create(new CreateCategoryRequest("Food"), 1L);

    assertThat(result).isEqualTo(response);
    verify(categoryRepository).save(any(Category.class));
  }

  @Test
  void create_throwsWhenUserNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> categoryService.create(new CreateCategoryRequest("Food"), 1L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("1");
  }

  @Test
  void update_appliesChangesAndSaves() {
    Category cat = new Category();
    Category saved = new Category();
    CategoryResponse response = new CategoryResponse(1L, "Updated", false);
    when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(cat));
    when(categoryRepository.save(cat)).thenReturn(saved);
    when(categoryMapper.toResponse(saved)).thenReturn(response);

    CategoryResponse result =
        categoryService.update(1L, 1L, new UpdateCategoryRequest("Updated", false));

    assertThat(result).isEqualTo(response);
  }

  @Test
  void delete_deletesCategory() {
    Category cat = new Category();
    when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(cat));

    categoryService.delete(1L, 1L);

    verify(categoryRepository).delete(cat);
  }

  @Test
  void delete_throwsWhenNotFound() {
    when(categoryRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> categoryService.delete(1L, 1L))
        .isInstanceOf(ResourceNotFoundException.class);
  }
}
