package com.walletapp.recurringtransaction.internal.domain;

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
@Table(name = "recurring_transaction")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class RecurringTransaction extends BaseEntity {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TransactionType type;

  @Column(nullable = false, precision = 19, scale = 4)
  private BigDecimal originalAmount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "original_currency_id", nullable = false)
  private Currency originalCurrency;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RecurringFrequency frequency;

  @Column(nullable = false)
  private LocalDate nextExecutionDate;

  @Column private LocalDate endDate;

  @Column(length = 255)
  private String description;

  @Column(nullable = false)
  private boolean active;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id", nullable = false)
  private Account account;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public static RecurringTransaction create(
      TransactionType type,
      BigDecimal originalAmount,
      Currency originalCurrency,
      RecurringFrequency frequency,
      LocalDate nextExecutionDate,
      LocalDate endDate,
      String description,
      Account account,
      Category category,
      User user) {
    RecurringTransaction rt = new RecurringTransaction();
    rt.type = type;
    rt.originalAmount = originalAmount;
    rt.originalCurrency = originalCurrency;
    rt.frequency = frequency;
    rt.nextExecutionDate = nextExecutionDate;
    rt.endDate = endDate;
    rt.description = description;
    rt.active = true;
    rt.account = account;
    rt.category = category;
    rt.user = user;
    return rt;
  }
}
