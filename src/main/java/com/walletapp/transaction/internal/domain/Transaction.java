package com.walletapp.transaction.internal.domain;

import com.walletapp.account.internal.domain.Account;
import com.walletapp.category.internal.domain.Category;
import com.walletapp.currency.internal.domain.Currency;
import com.walletapp.shared.BaseEntity;
import com.walletapp.shared.TransactionType;
import com.walletapp.user.internal.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Transaction extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionType type;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal originalAmount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "original_currency_id", nullable = false)
  private Currency originalCurrency;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal referenceAmount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reference_currency_id", nullable = false)
  private Currency referenceCurrency;

  @Column(nullable = false, precision = 19, scale = 6)
  private BigDecimal exchangeRate;

  @Column(nullable = false)
  private LocalDate date;

  @Column(length = 255)
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public static Transaction create(
      TransactionType type,
      BigDecimal originalAmount,
      Currency originalCurrency,
      BigDecimal referenceAmount,
      Currency referenceCurrency,
      BigDecimal exchangeRate,
      LocalDate date,
      String description,
      Account account,
      Category category,
      User user) {
    Transaction transaction = new Transaction();
    transaction.type = type;
    transaction.originalAmount = originalAmount;
    transaction.originalCurrency = originalCurrency;
    transaction.referenceAmount = referenceAmount;
    transaction.referenceCurrency = referenceCurrency;
    transaction.exchangeRate = exchangeRate;
    transaction.date = date;
    transaction.description = description;
    transaction.account = account;
    transaction.category = category;
    transaction.user = user;
    return transaction;
  }
}
