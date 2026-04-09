package com.walletapp.category.internal.domain;

import com.walletapp.shared.BaseEntity;
import com.walletapp.user.internal.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Category extends BaseEntity {

  @Column(nullable = false, length = 100)
  private String name;

  @Column(name = "icon_key", nullable = false, length = 50)
  private String iconKey;

  @Column(name = "color_key", nullable = false, length = 20)
  private String colorKey;

  @Column(nullable = false)
  private boolean active;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public static Category create(String name, String iconKey, String colorKey, User user) {
    Category category = new Category();
    category.name = name;
    category.iconKey = iconKey;
    category.colorKey = colorKey;
    category.user = user;
    category.active = true;
    return category;
  }
}
