package com.walletapp.category.web;

import com.walletapp.auth.internal.service.AuthService;
import com.walletapp.category.internal.service.CategoryService;
import com.walletapp.category.web.request.CreateCategoryRequest;
import com.walletapp.category.web.request.UpdateCategoryRequest;
import com.walletapp.category.web.response.CategoryResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;
  private final AuthService authService;

  @GetMapping
  public ResponseEntity<List<CategoryResponse>> findAll() {
    return ResponseEntity.ok(categoryService.findAllByUser(authService.getCurrentUserId()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
    return ResponseEntity.ok(categoryService.findById(id, authService.getCurrentUserId()));
  }

  @PostMapping
  public ResponseEntity<CategoryResponse> create(
      @Valid @RequestBody CreateCategoryRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(categoryService.create(request, authService.getCurrentUserId()));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<CategoryResponse> update(
      @PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
    return ResponseEntity.ok(categoryService.update(id, authService.getCurrentUserId(), request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    categoryService.delete(id, authService.getCurrentUserId());
    return ResponseEntity.noContent().build();
  }
}
