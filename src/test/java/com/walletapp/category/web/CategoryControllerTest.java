package com.walletapp.category.web;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walletapp.auth.internal.service.AuthService;
import com.walletapp.category.internal.service.CategoryService;
import com.walletapp.category.web.request.CreateCategoryRequest;
import com.walletapp.category.web.request.UpdateCategoryRequest;
import com.walletapp.category.web.response.CategoryResponse;
import com.walletapp.shared.exception.ResourceNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @MockitoBean CategoryService categoryService;
  @MockitoBean AuthService authService;

  @Test
  void findAll_returnsOk() throws Exception {
    when(authService.getCurrentUserId()).thenReturn(1L);
    when(categoryService.findAllByUser(1L))
        .thenReturn(List.of(new CategoryResponse(1L, "Food", true)));

    mockMvc
        .perform(get("/api/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("Food"));
  }

  @Test
  void findById_returnsOk() throws Exception {
    when(authService.getCurrentUserId()).thenReturn(1L);
    when(categoryService.findById(1L, 1L)).thenReturn(new CategoryResponse(1L, "Food", true));

    mockMvc
        .perform(get("/api/categories/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Food"));
  }

  @Test
  void findById_returnsNotFound() throws Exception {
    when(authService.getCurrentUserId()).thenReturn(1L);
    when(categoryService.findById(1L, 1L))
        .thenThrow(new ResourceNotFoundException("Category not found: 1"));

    mockMvc.perform(get("/api/categories/1")).andExpect(status().isNotFound());
  }

  @Test
  void create_returnsCreated() throws Exception {
    when(authService.getCurrentUserId()).thenReturn(1L);
    when(categoryService.create(new CreateCategoryRequest("Food"), 1L))
        .thenReturn(new CategoryResponse(1L, "Food", true));

    mockMvc
        .perform(
            post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateCategoryRequest("Food"))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Food"));
  }

  @Test
  void create_returnsBadRequestWhenNameBlank() throws Exception {
    mockMvc
        .perform(
            post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateCategoryRequest(""))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fieldErrors.name").exists());
  }

  @Test
  void update_returnsOk() throws Exception {
    when(authService.getCurrentUserId()).thenReturn(1L);
    UpdateCategoryRequest request = new UpdateCategoryRequest("Updated", null);
    when(categoryService.update(1L, 1L, request))
        .thenReturn(new CategoryResponse(1L, "Updated", true));

    mockMvc
        .perform(
            patch("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated"));
  }

  @Test
  void delete_returnsNoContent() throws Exception {
    when(authService.getCurrentUserId()).thenReturn(1L);

    mockMvc.perform(delete("/api/categories/1")).andExpect(status().isNoContent());
  }

  @Test
  void delete_returnsNotFoundWhenMissing() throws Exception {
    when(authService.getCurrentUserId()).thenReturn(1L);
    doThrow(new ResourceNotFoundException("Category not found: 1"))
        .when(categoryService)
        .delete(1L, 1L);

    mockMvc.perform(delete("/api/categories/1")).andExpect(status().isNotFound());
  }
}
