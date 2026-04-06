package com.walletapp.currency.internal.domain;

import com.walletapp.shared.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "currency")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Currency extends BaseEntity {

  @Column(nullable = false, unique = true, length = 3)
  private String code;

  @Column(nullable = false, length = 50)
  private String name;

  @Column(nullable = false, length = 5)
  private String symbol;

  @Column(nullable = false)
  private boolean active;

  public static Currency create(String code, String name, String symbol) {
    Currency currency = new Currency();
    currency.code = code.toUpperCase();
    currency.name = name;
    currency.symbol = symbol;
    currency.active = true;
    return currency;
  }
}
