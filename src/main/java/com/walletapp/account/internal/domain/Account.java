package com.walletapp.account.internal.domain;

import com.walletapp.currency.internal.domain.Currency;
import com.walletapp.shared.BaseEntity;
import com.walletapp.user.internal.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "account")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Account extends BaseEntity {

  @Column(nullable = false, length = 100)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AccountType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "currency_id", nullable = false)
  private Currency currency;

  @Column(nullable = false)
  private boolean active;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public static Account create(String name, AccountType type, Currency currency, User user) {
    Account account = new Account();
    account.name = name;
    account.type = type;
    account.currency = currency;
    account.user = user;
    account.active = true;
    return account;
  }
}
