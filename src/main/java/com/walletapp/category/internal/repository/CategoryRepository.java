package com.walletapp.category.internal.repository;

import com.walletapp.category.internal.domain.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  List<Category> findAllByUserId(Long userId);

  Optional<Category> findByIdAndUserId(Long id, Long userId);
}
